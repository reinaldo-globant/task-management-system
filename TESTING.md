# Integration Testing Guide

This guide explains how to run the integration tests for the task management system.

## Overview

The integration tests verify the complete functionality of:
- User registration and authentication flow
- Task creation, retrieval, and management
- Service-to-service communication
- Data persistence and cleanup

## Test Data Patterns

All test data follows these patterns for easy identification and cleanup:

- **Test Users**: `test-user-{timestamp}-{id}@test.example.com`
- **Test Tasks**: `test-task-{timestamp}-{id}`
- **Test Run ID**: `{YYYYMMDD-HHMMSS}-{counter}`

This pattern ensures:
1. Easy identification of test data
2. Automatic cleanup after tests
3. No conflicts with production data
4. Unique identifiers across test runs

## Prerequisites

Before running integration tests, ensure:

1. **Services are running**:
   ```bash
   # Terminal 1: Start user service
   cd user-service
   mvn spring-boot:run
   
   # Terminal 2: Start task backend
   cd task-backend
   mvn spring-boot:run
   ```

2. **Services are healthy**:
   - User Service: http://localhost:8081/actuator/health
   - Task Backend: http://localhost:8080/actuator/health

## Running Tests

### Option 1: Quick Test Run (Recommended)

```bash
# From project root
./run-tests.sh
```

This script will:
- ✅ Check if services are running
- ✅ Run user service unit tests
- ✅ Run integration tests
- ✅ Clean up test data
- ✅ Report results

### Option 2: Manual Test Execution

```bash
# Run user service gRPC tests
cd user-service
mvn test -Dtest="*Grpc*Test"

# Run integration tests
cd ../task-backend
mvn test -Dtest="*IntegrationTest" -Dspring.profiles.active=test

# Clean up test data
mvn exec:java -Dexec.mainClass="com.taskmanagement.taskbackend.integration.TestDataCleaner"
```

### Option 3: Full Test Suite with Service Management

```bash
# From project root (advanced users)
./test-runner.sh
```

This script can:
- Start services if not running
- Run all tests
- Clean up test data
- Stop services if started by script

## Test Classes

### BaseIntegrationTest
- Base class for all integration tests
- Provides test data generation utilities
- Handles service health checks
- Manages test data cleanup
- Creates authenticated HTTP requests

### UserRegistrationIntegrationTest
Tests the complete user registration flow:
- ✅ Successful user registration and login
- ✅ Duplicate username/email validation
- ✅ Invalid data validation
- ✅ JWT token generation and validation

### TaskCreationIntegrationTest
Tests the complete task management flow:
- ✅ Authenticated task creation
- ✅ Task retrieval by owner
- ✅ Task status updates
- ✅ Authentication requirement validation
- ✅ Invalid data validation

## Test Data Cleanup

Test data is automatically cleaned up using these methods:

### Automatic Cleanup
- Runs after each test method (`@AfterEach`)
- Runs before each test method (`@BeforeEach`)
- Uses test data patterns to identify test records

### Manual Cleanup
```bash
cd task-backend
mvn exec:java -Dexec.mainClass="com.taskmanagement.taskbackend.integration.TestDataCleaner"
```

### Database Cleanup Queries
```sql
-- Clean up test tasks and related data
DELETE FROM status_changes 
WHERE task_id IN (SELECT id FROM tasks WHERE title LIKE 'test-task-%');

DELETE FROM tasks WHERE title LIKE 'test-task-%';

-- Note: User cleanup is handled by user-service
```

## Configuration

### Test Properties
Location: `task-backend/src/test/resources/application-test.properties`

Key configurations:
```properties
# Test profile
spring.profiles.active=test

# In-memory H2 database for tests
spring.datasource.url=jdbc:h2:mem:testdb

# User service endpoints
user.service.url=http://localhost:8081
user.service.validation.endpoint=/api/auth/validate
```

### Environment Variables
```bash
# Optional: Override service URLs
export USER_SERVICE_URL=http://localhost:8081
export TASK_BACKEND_URL=http://localhost:8080
```

## Troubleshooting

### Common Issues

1. **Services not running**
   ```
   ❌ User service is not running. Please start it first:
      cd user-service && mvn spring-boot:run
   ```

2. **Port conflicts**
   ```bash
   # Check what's running on ports
   lsof -i :8080
   lsof -i :8081
   ```

3. **Database connection issues**
   - Tests use H2 in-memory database
   - Check `application-test.properties`
   - Ensure no port conflicts with existing databases

4. **Test data conflicts**
   ```bash
   # Manual cleanup
   cd task-backend
   mvn exec:java -Dexec.mainClass="com.taskmanagement.taskbackend.integration.TestDataCleaner"
   ```

### Debug Mode

Run tests with debug logging:
```bash
mvn test -Dtest="*IntegrationTest" -Dspring.profiles.active=test -Dlogging.level.com.taskmanagement=DEBUG
```

## CI/CD Integration

For continuous integration pipelines:

```yaml
# Example GitHub Actions workflow
- name: Run Integration Tests
  run: |
    # Start services
    cd user-service && mvn spring-boot:start -Dspring-boot.run.fork=true &
    cd task-backend && mvn spring-boot:start -Dspring-boot.run.fork=true &
    
    # Wait for services
    sleep 30
    
    # Run tests
    ./run-tests.sh
    
    # Cleanup is automatic
```

## Best Practices

1. **Always clean up test data** - Use the provided cleanup utilities
2. **Use test data patterns** - Follow the naming conventions
3. **Run tests before deployment** - Verify system integration
4. **Check service health** - Ensure services are ready before testing
5. **Monitor test execution** - Review logs for any issues

## Test Coverage

Current integration test coverage:
- ✅ User registration and authentication
- ✅ Task CRUD operations
- ✅ Authentication and authorization
- ✅ Service-to-service communication
- ✅ Data validation and error handling
- ✅ Test data management and cleanup

## Next Steps

To extend the test suite:
1. Add more test scenarios in existing test classes
2. Create new integration test classes for additional features
3. Add performance testing for high-load scenarios
4. Implement contract testing between services