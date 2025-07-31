#!/bin/bash

# Integration Test Runner
# This script runs integration tests before starting containers

set -e

echo "🧪 Starting Integration Test Suite"
echo "=================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if services are running (we need them for integration tests)
check_services() {
    print_status "Checking if required services are running..."
    
    # Check user-service
    if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
        print_success "User service is running on port 8081"
    else
        print_warning "User service is not running on port 8081"
        print_status "Starting user service..."
        
        cd user-service
        mvn spring-boot:start -Dspring-boot.run.fork=true &
        USER_SERVICE_PID=$!
        cd ..
        
        # Wait for user service to start
        for i in {1..30}; do
            if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
                print_success "User service started successfully"
                break
            fi
            if [ $i -eq 30 ]; then
                print_error "User service failed to start"
                exit 1
            fi
            sleep 2
        done
    fi
    
    # Check task-backend
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        print_success "Task backend is running on port 8080"
    else
        print_warning "Task backend is not running on port 8080"
        print_status "Starting task backend..."
        
        cd task-backend
        mvn spring-boot:start -Dspring-boot.run.fork=true &
        TASK_BACKEND_PID=$!
        cd ..
        
        # Wait for task backend to start
        for i in {1..30}; do
            if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
                print_success "Task backend started successfully"
                break
            fi
            if [ $i -eq 30 ]; then
                print_error "Task backend failed to start"
                exit 1
            fi
            sleep 2
        done
    fi
}

# Run tests for user service
run_user_service_tests() {
    print_status "Running user service tests..."
    
    cd user-service
    if mvn test -Dtest="*Grpc*Test" -q; then
        print_success "User service tests passed"
    else
        print_error "User service tests failed"
        cd ..
        return 1
    fi
    cd ..
}

# Run integration tests
run_integration_tests() {
    print_status "Running integration tests..."
    
    cd task-backend
    if mvn test -Dtest="*IntegrationTest" -Dspring.profiles.active=test; then
        print_success "Integration tests passed"
    else
        print_error "Integration tests failed"
        cd ..
        return 1
    fi
    cd ..
}

# Cleanup test data
cleanup_test_data() {
    print_status "Cleaning up test data..."
    
    # Connect to databases and clean up test data
    # This is a placeholder - you might want to implement specific cleanup logic
    cd task-backend
    mvn exec:java -Dexec.mainClass="com.taskmanagement.taskbackend.integration.TestDataCleaner" -q 2>/dev/null || true
    cd ..
    
    print_success "Test data cleanup completed"
}

# Main execution
main() {
    print_status "Starting pre-container test suite..."
    
    # Check if services are available
    check_services
    
    # Run user service tests
    if ! run_user_service_tests; then
        print_error "User service tests failed. Aborting."
        exit 1
    fi
    
    # Run integration tests
    if ! run_integration_tests; then
        print_error "Integration tests failed. Aborting."
        cleanup_test_data
        exit 1
    fi
    
    # Cleanup test data
    cleanup_test_data
    
    print_success "All tests passed! Ready to start containers."
    echo ""
    print_status "You can now run: docker-compose up"
    echo ""
}

# Handle script interruption
cleanup_on_exit() {
    print_warning "Script interrupted. Cleaning up..."
    cleanup_test_data
    
    # Stop services if we started them
    if [ ! -z "$USER_SERVICE_PID" ]; then
        kill $USER_SERVICE_PID 2>/dev/null || true
    fi
    if [ ! -z "$TASK_BACKEND_PID" ]; then
        kill $TASK_BACKEND_PID 2>/dev/null || true
    fi
    
    exit 1
}

# Set up signal handlers
trap cleanup_on_exit SIGINT SIGTERM

# Run main function
main "$@"