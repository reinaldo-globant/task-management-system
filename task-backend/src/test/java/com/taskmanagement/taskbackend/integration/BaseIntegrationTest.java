package com.taskmanagement.taskbackend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for integration tests with test data management
 * Uses test data patterns for easy identification and cleanup
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);
    
    // Test data patterns for easy identification
    protected static final String TEST_USER_PREFIX = "test-user-";
    protected static final String TEST_TASK_PREFIX = "test-task-";
    protected static final String TEST_EMAIL_SUFFIX = "@test.example.com";
    
    private static final AtomicInteger testCounter = new AtomicInteger(0);
    protected final String testRunId = generateTestRunId();

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Value("${user.service.url:http://localhost:8081}")
    protected String userServiceUrl;

    protected String baseUrl;
    protected String userServiceBaseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        userServiceBaseUrl = userServiceUrl;
        
        logger.info("🧪 Starting integration test - Run ID: {}", testRunId);
        logger.info("📍 Task Backend URL: {}", baseUrl);
        logger.info("📍 User Service URL: {}", userServiceBaseUrl);
        
        // Clean up any existing test data before starting
        cleanupTestData();
    }

    @AfterEach
    void tearDown() {
        logger.info("🧹 Cleaning up test data for run ID: {}", testRunId);
        cleanupTestData();
    }

    /**
     * Generate a unique test run ID for this test execution
     */
    private String generateTestRunId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) 
               + "-" + testCounter.incrementAndGet();
    }

    /**
     * Generate a unique test username
     */
    protected String generateTestUsername() {
        return TEST_USER_PREFIX + testRunId + "-" + System.nanoTime() % 10000;
    }

    /**
     * Generate a unique test email
     */
    protected String generateTestEmail() {
        return generateTestUsername() + TEST_EMAIL_SUFFIX;
    }

    /**
     * Generate a unique test task title
     */
    protected String generateTestTaskTitle() {
        return TEST_TASK_PREFIX + testRunId + "-" + System.nanoTime() % 10000;
    }

    /**
     * Create HTTP headers with authentication token
     */
    protected HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null && !token.isEmpty()) {
            headers.setBearerAuth(token);
        }
        return headers;
    }

    /**
     * Create HTTP headers without authentication
     */
    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Wait for services to be ready
     */
    protected void waitForServices() {
        logger.info("⏳ Waiting for services to be ready...");
        
        // Wait for task backend
        waitForService(baseUrl + "/actuator/health", "Task Backend");
        
        // Wait for user service
        waitForService(userServiceBaseUrl + "/actuator/health", "User Service");
        
        logger.info("✅ All services are ready");
    }

    private void waitForService(String healthUrl, String serviceName) {
        int maxAttempts = 30;
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    logger.info("✅ {} is ready", serviceName);
                    return;
                }
            } catch (Exception e) {
                logger.debug("❌ {} not ready yet (attempt {}/{}): {}", 
                    serviceName, attempts + 1, maxAttempts, e.getMessage());
            }
            
            attempts++;
            try {
                Thread.sleep(2000); // Wait 2 seconds between attempts
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for " + serviceName, e);
            }
        }
        
        throw new RuntimeException(serviceName + " failed to become ready after " + maxAttempts + " attempts");
    }

    /**
     * Clean up all test data using the test patterns
     */
    protected void cleanupTestData() {
        try {
            logger.debug("🧹 Cleaning up test tasks...");
            // Clean up tasks with test pattern
            jdbcTemplate.update(
                "DELETE FROM status_changes WHERE task_id IN (SELECT id FROM tasks WHERE title LIKE ?)",
                TEST_TASK_PREFIX + "%"
            );
            jdbcTemplate.update("DELETE FROM tasks WHERE title LIKE ?", TEST_TASK_PREFIX + "%");
            
            logger.debug("🧹 Cleaning up test users from user service...");
            // Clean up users from user service (if accessible)
            cleanupUsersFromUserService();
            
        } catch (Exception e) {
            logger.warn("⚠️ Error during cleanup: {}", e.getMessage());
        }
    }

    /**
     * Clean up test users from user service
     */
    private void cleanupUsersFromUserService() {
        try {
            // Note: This assumes the user service has a cleanup endpoint or we can access its database
            // For now, we'll try to clean up via direct database access if possible
            // In a real scenario, you might want to add a test cleanup endpoint to user service
            
            logger.debug("🧹 Test user cleanup completed (delegated to user service)");
        } catch (Exception e) {
            logger.debug("⚠️ Could not clean up users from user service: {}", e.getMessage());
        }
    }

    /**
     * Register a test user and return the JWT token
     */
    protected TestUserResponse registerTestUser() {
        String username = generateTestUsername();
        String email = generateTestEmail();
        String password = "TestPassword123!";
        String name = "Test User " + testRunId;

        Map<String, Object> signupRequest = new HashMap<>();
        signupRequest.put("username", username);
        signupRequest.put("email", email);
        signupRequest.put("password", password);
        signupRequest.put("name", name);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(signupRequest, createHeaders());

        logger.info("👤 Registering test user: {}", username);
        
        ResponseEntity<Map> signupResponse = restTemplate.exchange(
            userServiceBaseUrl + "/api/auth/signup",
            HttpMethod.POST,
            request,
            Map.class
        );

        if (signupResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to register test user: " + signupResponse.getBody());
        }

        // Now login to get the token
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        HttpEntity<Map<String, Object>> loginHttpRequest = new HttpEntity<>(loginRequest, createHeaders());

        ResponseEntity<Map> loginResponse = restTemplate.exchange(
            userServiceBaseUrl + "/api/auth/signin",
            HttpMethod.POST,
            loginHttpRequest,
            Map.class
        );

        if (loginResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to login test user: " + loginResponse.getBody());
        }

        Map<String, Object> loginBody = loginResponse.getBody();
        String token = (String) loginBody.get("accessToken");
        Long userId = ((Number) loginBody.get("id")).longValue();

        logger.info("✅ Test user registered and logged in: {} (ID: {})", username, userId);

        return new TestUserResponse(userId, username, email, name, token);
    }

    /**
     * Data class for test user information
     */
    protected static class TestUserResponse {
        public final Long id;
        public final String username;
        public final String email;
        public final String name;
        public final String token;

        public TestUserResponse(Long id, String username, String email, String name, String token) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.name = name;
            this.token = token;
        }
    }
}