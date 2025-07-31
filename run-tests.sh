#!/bin/bash

# Simple test runner for integration tests
# Usage: ./run-tests.sh

set -e

echo "🧪 Running Integration Tests"
echo "============================"

# Function to check if a service is running
check_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            echo "✅ $service_name is ready"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo "⏳ Waiting for $service_name... (attempt $attempt/$max_attempts)"
        sleep 2
    done
    
    echo "❌ $service_name failed to become ready"
    return 1
}

echo "📍 Checking services..."

# Check if user service is running
if ! check_service "http://localhost:8081/actuator/health" "User Service"; then
    echo "⚠️ User service is not running. Please start it first:"
    echo "   cd user-service && mvn spring-boot:run"
    exit 1
fi

# Check if task backend is running  
if ! check_service "http://localhost:8080/actuator/health" "Task Backend"; then
    echo "⚠️ Task backend is not running. Please start it first:"
    echo "   cd task-backend && mvn spring-boot:run"
    exit 1
fi

echo ""
echo "🧪 Running Unit Tests..."
echo "========================"

# Run user service tests
echo "📝 Running user service gRPC tests..."
cd user-service
if mvn test -Dtest="*Grpc*Test" -q; then
    echo "✅ User service tests passed"
else
    echo "❌ User service tests failed"
    exit 1
fi
cd ..

echo ""
echo "🧪 Running Integration Tests..."
echo "==============================="

# Run integration tests
echo "📝 Running task backend integration tests..."
cd task-backend
if mvn test -Dtest="*IntegrationTest" -Dspring.profiles.active=test; then
    echo "✅ Integration tests passed"
else
    echo "❌ Integration tests failed"
    exit 1
fi
cd ..

echo ""
echo "🧹 Cleaning up test data..."
cd task-backend
mvn exec:java -Dexec.mainClass="com.taskmanagement.taskbackend.integration.TestDataCleaner" -q 2>/dev/null || echo "⚠️ Cleanup utility not available or failed"
cd ..

echo ""
echo "🎉 All tests passed successfully!"
echo "✅ Ready for deployment"
echo ""