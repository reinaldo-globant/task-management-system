package com.taskmanagement.taskbackend.integration;

import com.taskmanagement.taskbackend.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for task creation flow
 * Tests the complete task creation process including authentication
 */
public class TaskCreationIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setUpTaskCreationTest() {
        // Wait for services to be ready before running tests
        waitForServices();
    }

    @Test
    void testTaskCreation_CompleteFlow_ShouldSucceed() {
        logger.info("🧪 TEST: Complete task creation flow");
        
        // Arrange - Register and login a test user
        TestUserResponse testUser = registerTestUser();
        String taskTitle = generateTestTaskTitle();
        String taskDescription = "Integration test task description for " + testRunId;

        Map<String, Object> taskRequest = new HashMap<>();
        taskRequest.put("title", taskTitle);
        taskRequest.put("description", taskDescription);
        taskRequest.put("status", TaskStatus.TODO.name());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(taskRequest, createAuthHeaders(testUser.token));

        logger.info("📝 Creating task '{}' for user: {}", taskTitle, testUser.username);

        // Act - Create task
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/api/tasks",
            HttpMethod.POST,
            request,
            Map.class
        );

        // Assert - Task creation successful
        logger.info("📊 Task creation response status: {}", response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Task creation should succeed");
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody, "Task creation response body should not be null");
        
        // Verify task details
        assertNotNull(responseBody.get("id"), "Task ID should be returned");
        assertEquals(taskTitle, responseBody.get("title"), "Task title should match");
        assertEquals(taskDescription, responseBody.get("description"), "Task description should match");
        assertEquals(TaskStatus.TODO.name(), responseBody.get("status"), "Task status should be TODO");
        assertEquals(testUser.id, ((Number) responseBody.get("ownerId")).longValue(), "Owner ID should match");
        assertEquals(testUser.username, responseBody.get("owner"), "Owner username should match");
        assertNotNull(responseBody.get("createdAt"), "Created timestamp should be set");

        logger.info("✅ Task created successfully with ID: {}", responseBody.get("id"));
        logger.info("🎉 Complete task creation flow test PASSED");
    }

    @Test
    void testTaskCreation_WithoutAuthentication_ShouldFail() {
        logger.info("🧪 TEST: Task creation without authentication should fail");
        
        // Arrange - Create task request without authentication
        String taskTitle = generateTestTaskTitle();
        String taskDescription = "Unauthorized task creation test";

        Map<String, Object> taskRequest = new HashMap<>();
        taskRequest.put("title", taskTitle);
        taskRequest.put("description", taskDescription);
        taskRequest.put("status", TaskStatus.TODO.name());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(taskRequest, createHeaders());

        logger.info("📝 Attempting to create task without authentication: {}", taskTitle);

        // Act - Attempt to create task without token
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/api/tasks",
            HttpMethod.POST,
            request,
            Map.class
        );

        // Assert - Should fail with unauthorized
        logger.info("📊 Unauthorized task creation response status: {}", response.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), 
            "Task creation without authentication should fail with 401");
        
        logger.info("✅ Unauthorized task creation correctly rejected");
        logger.info("🎉 Authentication required test PASSED");
    }

    @Test
    void testTaskCreation_WithInvalidToken_ShouldFail() {
        logger.info("🧪 TEST: Task creation with invalid token should fail");
        
        // Arrange - Create task request with invalid token
        String taskTitle = generateTestTaskTitle();
        String taskDescription = "Invalid token task creation test";
        String invalidToken = "invalid.jwt.token.here";

        Map<String, Object> taskRequest = new HashMap<>();
        taskRequest.put("title", taskTitle);
        taskRequest.put("description", taskDescription);
        taskRequest.put("status", TaskStatus.TODO.name());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(taskRequest, createAuthHeaders(invalidToken));

        logger.info("📝 Attempting to create task with invalid token: {}", taskTitle);

        // Act - Attempt to create task with invalid token
        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/api/tasks",
            HttpMethod.POST,
            request,
            Map.class
        );

        // Assert - Should fail with unauthorized
        logger.info("📊 Invalid token task creation response status: {}", response.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), 
            "Task creation with invalid token should fail with 401");
        
        logger.info("✅ Invalid token task creation correctly rejected");
        logger.info("🎉 Invalid token test PASSED");
    }

    @Test
    void testTaskCreation_InvalidData_ShouldFail() {
        logger.info("🧪 TEST: Task creation with invalid data should fail");
        
        // Arrange - Register and login a test user
        TestUserResponse testUser = registerTestUser();

        // Test cases for invalid data
        Object[][] invalidTestCases = {
            {"", "Valid description", TaskStatus.TODO.name()}, // Empty title
            {null, "Valid description", TaskStatus.TODO.name()}, // Null title
            {generateTestTaskTitle(), "Valid description", "INVALID_STATUS"}, // Invalid status
            {generateTestTaskTitle(), "Valid description", null}, // Null status
        };

        String[] testCaseNames = {
            "empty title",
            "null title", 
            "invalid status",
            "null status"
        };

        for (int i = 0; i < invalidTestCases.length; i++) {
            Object[] testCase = invalidTestCases[i];
            String testCaseName = testCaseNames[i];
            
            logger.info("🔍 Testing invalid case: {}", testCaseName);

            Map<String, Object> taskRequest = new HashMap<>();
            taskRequest.put("title", testCase[0]);
            taskRequest.put("description", testCase[1]);
            taskRequest.put("status", testCase[2]);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(taskRequest, createAuthHeaders(testUser.token));

            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/tasks",
                HttpMethod.POST,
                request,
                Map.class
            );

            logger.info("📊 Invalid data test '{}' response status: {}", testCaseName, response.getStatusCode());
            assertTrue(response.getStatusCode().is4xxClientError(), 
                "Task creation with " + testCaseName + " should fail with 4xx error");
            
            logger.info("✅ Invalid case '{}' correctly rejected", testCaseName);
        }

        logger.info("🎉 Invalid data validation tests PASSED");
    }

    @Test
    void testTaskRetrieval_ByOwner_ShouldSucceed() {
        logger.info("🧪 TEST: Task retrieval by owner should succeed");
        
        // Arrange - Register user and create multiple test tasks
        TestUserResponse testUser = registerTestUser();
        String taskTitle1 = generateTestTaskTitle();
        String taskTitle2 = generateTestTaskTitle();
        
        // Create first task
        Map<String, Object> task1Request = new HashMap<>();
        task1Request.put("title", taskTitle1);
        task1Request.put("description", "First test task");
        task1Request.put("status", TaskStatus.TODO.name());

        HttpEntity<Map<String, Object>> request1 = new HttpEntity<>(task1Request, createAuthHeaders(testUser.token));
        
        ResponseEntity<Map> createResponse1 = restTemplate.exchange(
            baseUrl + "/api/tasks",
            HttpMethod.POST,
            request1,
            Map.class
        );
        assertEquals(HttpStatus.CREATED, createResponse1.getStatusCode());
        
        // Create second task
        Map<String, Object> task2Request = new HashMap<>();
        task2Request.put("title", taskTitle2);
        task2Request.put("description", "Second test task");
        task2Request.put("status", TaskStatus.IN_PROGRESS.name());

        HttpEntity<Map<String, Object>> request2 = new HttpEntity<>(task2Request, createAuthHeaders(testUser.token));
        
        ResponseEntity<Map> createResponse2 = restTemplate.exchange(
            baseUrl + "/api/tasks",
            HttpMethod.POST,
            request2,
            Map.class
        );
        assertEquals(HttpStatus.CREATED, createResponse2.getStatusCode());

        logger.info("✅ Created test tasks: '{}' and '{}'", taskTitle1, taskTitle2);

        // Act - Retrieve tasks by owner
        HttpEntity<Void> getRequest = new HttpEntity<>(createAuthHeaders(testUser.token));
        
        ResponseEntity<List> response = restTemplate.exchange(
            baseUrl + "/api/tasks/owner/" + testUser.username,
            HttpMethod.GET,
            getRequest,
            List.class
        );

        // Assert - Should retrieve the created tasks
        logger.info("📊 Task retrieval response status: {}", response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Task retrieval should succeed");
        
        List<Map<String, Object>> tasks = (List<Map<String, Object>>) response.getBody();
        assertNotNull(tasks, "Tasks list should not be null");
        assertTrue(tasks.size() >= 2, "Should retrieve at least the 2 created tasks");
        
        // Verify our test tasks are in the response
        boolean foundTask1 = tasks.stream().anyMatch(task -> taskTitle1.equals(task.get("title")));
        boolean foundTask2 = tasks.stream().anyMatch(task -> taskTitle2.equals(task.get("title")));
        
        assertTrue(foundTask1, "Should find first test task in response");
        assertTrue(foundTask2, "Should find second test task in response");
        
        logger.info("✅ Successfully retrieved {} tasks for user: {}", tasks.size(), testUser.username);
        logger.info("🎉 Task retrieval test PASSED");
    }

    @Test
    void testTaskStatusUpdate_ShouldSucceed() {
        logger.info("🧪 TEST: Task status update should succeed");
        
        // Arrange - Register user and create a test task
        TestUserResponse testUser = registerTestUser();
        String taskTitle = generateTestTaskTitle();
        
        // Create task
        Map<String, Object> taskRequest = new HashMap<>();
        taskRequest.put("title", taskTitle);
        taskRequest.put("description", "Task for status update test");
        taskRequest.put("status", TaskStatus.TODO.name());

        HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(taskRequest, createAuthHeaders(testUser.token));
        
        ResponseEntity<Map> createResponse = restTemplate.exchange(
            baseUrl + "/api/tasks",
            HttpMethod.POST,
            createRequest,
            Map.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        
        Long taskId = ((Number) createResponse.getBody().get("id")).longValue();
        logger.info("✅ Created test task with ID: {}", taskId);

        // Act - Update task status
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("title", taskTitle);
        updateRequest.put("description", "Task for status update test");
        updateRequest.put("status", TaskStatus.DONE.name());

        HttpEntity<Map<String, Object>> putRequest = new HttpEntity<>(updateRequest, createAuthHeaders(testUser.token));
        
        ResponseEntity<Map> updateResponse = restTemplate.exchange(
            baseUrl + "/api/tasks/" + taskId,
            HttpMethod.PUT,
            putRequest,
            Map.class
        );

        // Assert - Status update should succeed
        logger.info("📊 Task update response status: {}", updateResponse.getStatusCode());
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode(), "Task update should succeed");
        
        Map<String, Object> updatedTask = updateResponse.getBody();
        assertNotNull(updatedTask, "Updated task should not be null");
        assertEquals(TaskStatus.DONE.name(), updatedTask.get("status"), "Task status should be updated to DONE");
        assertNotNull(updatedTask.get("updatedAt"), "Updated timestamp should be set");
        
        logger.info("✅ Task status updated successfully to: {}", updatedTask.get("status"));
        logger.info("🎉 Task status update test PASSED");
    }
}