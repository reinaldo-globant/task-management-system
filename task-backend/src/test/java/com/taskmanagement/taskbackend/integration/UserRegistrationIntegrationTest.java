package com.taskmanagement.taskbackend.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for user registration flow
 * Tests the complete user registration process with the user service
 */
@TestPropertySource(properties = {
    "user.service.url=http://localhost:8081",
    "user.service.validation.endpoint=/api/auth/validate"
})
public class UserRegistrationIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setUpUserRegistrationTest() {
        // Wait for services to be ready before running tests
        waitForServices();
    }

    @Test
    void testUserRegistration_CompleteFlow_ShouldSucceed() {
        logger.info("🧪 TEST: Complete user registration flow");
        
        // Arrange
        String username = generateTestUsername();
        String email = generateTestEmail();
        String password = "TestPassword123!";
        String name = "Integration Test User";

        Map<String, Object> signupRequest = new HashMap<>();
        signupRequest.put("username", username);
        signupRequest.put("email", email);
        signupRequest.put("password", password);
        signupRequest.put("name", name);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(signupRequest, createHeaders());

        logger.info("👤 Attempting to register user: {}", username);

        // Act - Register user
        ResponseEntity<Map> signupResponse = restTemplate.exchange(
            userServiceBaseUrl + "/api/auth/signup",
            HttpMethod.POST,
            request,
            Map.class
        );

        // Assert - Registration successful
        logger.info("📊 Registration response status: {}", signupResponse.getStatusCode());
        assertEquals(HttpStatus.OK, signupResponse.getStatusCode(), "User registration should succeed");
        
        Map<String, Object> signupBody = signupResponse.getBody();
        assertNotNull(signupBody, "Registration response body should not be null");
        
        logger.info("✅ User registration successful: {}", username);

        // Act - Login with registered user
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        HttpEntity<Map<String, Object>> loginHttpRequest = new HttpEntity<>(loginRequest, createHeaders());

        logger.info("🔐 Attempting to login user: {}", username);

        ResponseEntity<Map> loginResponse = restTemplate.exchange(
            userServiceBaseUrl + "/api/auth/signin",
            HttpMethod.POST,
            loginHttpRequest,
            Map.class
        );

        // Assert - Login successful
        logger.info("📊 Login response status: {}", loginResponse.getStatusCode());
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "User login should succeed");
        
        Map<String, Object> loginBody = loginResponse.getBody();
        assertNotNull(loginBody, "Login response body should not be null");
        
        // Verify JWT token is returned
        String accessToken = (String) loginBody.get("accessToken");
        assertNotNull(accessToken, "Access token should be returned");
        assertFalse(accessToken.isEmpty(), "Access token should not be empty");
        
        // Verify user details in response
        assertEquals(username, loginBody.get("username"), "Username should match");
        assertEquals(email, loginBody.get("email"), "Email should match");
        assertEquals(name, loginBody.get("name"), "Name should match");
        assertNotNull(loginBody.get("id"), "User ID should be returned");

        logger.info("✅ User login successful with token: {}...", accessToken.substring(0, 20));
        logger.info("🎉 Complete user registration flow test PASSED");
    }

    @Test
    void testUserRegistration_DuplicateUsername_ShouldFail() {
        logger.info("🧪 TEST: User registration with duplicate username should fail");
        
        // Arrange - Register first user
        String username = generateTestUsername();
        String email1 = generateTestEmail();
        String email2 = generateTestEmail(); // Different email, same username
        String password = "TestPassword123!";
        String name = "Test User";

        // First registration
        Map<String, Object> firstSignupRequest = new HashMap<>();
        firstSignupRequest.put("username", username);
        firstSignupRequest.put("email", email1);
        firstSignupRequest.put("password", password);
        firstSignupRequest.put("name", name);

        HttpEntity<Map<String, Object>> firstRequest = new HttpEntity<>(firstSignupRequest, createHeaders());

        logger.info("👤 Registering first user with username: {}", username);

        ResponseEntity<Map> firstResponse = restTemplate.exchange(
            userServiceBaseUrl + "/api/auth/signup",
            HttpMethod.POST,
            firstRequest,
            Map.class
        );

        assertEquals(HttpStatus.OK, firstResponse.getStatusCode(), "First registration should succeed");
        logger.info("✅ First user registered successfully");

        // Act - Attempt second registration with same username
        Map<String, Object> secondSignupRequest = new HashMap<>();
        secondSignupRequest.put("username", username); // Same username
        secondSignupRequest.put("email", email2);      // Different email
        secondSignupRequest.put("password", password);
        secondSignupRequest.put("name", name);

        HttpEntity<Map<String, Object>> secondRequest = new HttpEntity<>(secondSignupRequest, createHeaders());

        logger.info("👤 Attempting to register duplicate username: {}", username);

        ResponseEntity<Map> secondResponse = restTemplate.exchange(
            userServiceBaseUrl + "/api/auth/signup",
            HttpMethod.POST,
            secondRequest,
            Map.class
        );

        // Assert - Second registration should fail
        logger.info("📊 Duplicate registration response status: {}", secondResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, secondResponse.getStatusCode(), 
            "Duplicate username registration should fail");
        
        Map<String, Object> errorBody = secondResponse.getBody();
        assertNotNull(errorBody, "Error response body should not be null");
        
        logger.info("✅ Duplicate username registration correctly rejected");
        logger.info("🎉 Duplicate username test PASSED");
    }

    @Test
    void testUserRegistration_DuplicateEmail_ShouldFail() {
        logger.info("🧪 TEST: User registration with duplicate email should fail");
        
        // Arrange - Register first user
        String username1 = generateTestUsername();
        String username2 = generateTestUsername(); // Different username
        String email = generateTestEmail();        // Same email
        String password = "TestPassword123!";
        String name = "Test User";

        // First registration
        Map<String, Object> firstSignupRequest = new HashMap<>();
        firstSignupRequest.put("username", username1);
        firstSignupRequest.put("email", email);
        firstSignupRequest.put("password", password);
        firstSignupRequest.put("name", name);

        HttpEntity<Map<String, Object>> firstRequest = new HttpEntity<>(firstSignupRequest, createHeaders());

        logger.info("👤 Registering first user with email: {}", email);

        ResponseEntity<Map> firstResponse = restTemplate.exchange(
            userServiceBaseUrl + "/api/auth/signup",
            HttpMethod.POST,
            firstRequest,
            Map.class
        );

        assertEquals(HttpStatus.OK, firstResponse.getStatusCode(), "First registration should succeed");
        logger.info("✅ First user registered successfully");

        // Act - Attempt second registration with same email
        Map<String, Object> secondSignupRequest = new HashMap<>();
        secondSignupRequest.put("username", username2); // Different username
        secondSignupRequest.put("email", email);        // Same email
        secondSignupRequest.put("password", password);
        secondSignupRequest.put("name", name);

        HttpEntity<Map<String, Object>> secondRequest = new HttpEntity<>(secondSignupRequest, createHeaders());

        logger.info("👤 Attempting to register duplicate email: {}", email);

        ResponseEntity<Map> secondResponse = restTemplate.exchange(
            userServiceBaseUrl + "/api/auth/signup",
            HttpMethod.POST,
            secondRequest,
            Map.class
        );

        // Assert - Second registration should fail
        logger.info("📊 Duplicate email registration response status: {}", secondResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, secondResponse.getStatusCode(), 
            "Duplicate email registration should fail");
        
        Map<String, Object> errorBody = secondResponse.getBody();
        assertNotNull(errorBody, "Error response body should not be null");
        
        logger.info("✅ Duplicate email registration correctly rejected");
        logger.info("🎉 Duplicate email test PASSED");
    }

    @Test
    void testUserRegistration_InvalidData_ShouldFail() {
        logger.info("🧪 TEST: User registration with invalid data should fail");
        
        // Test cases for invalid data
        String[][] invalidTestCases = {
            {"", generateTestEmail(), "TestPassword123!", "Test User"}, // Empty username
            {generateTestUsername(), "invalid-email", "TestPassword123!", "Test User"}, // Invalid email
            {generateTestUsername(), generateTestEmail(), "weak", "Test User"}, // Weak password
            {generateTestUsername(), generateTestEmail(), "TestPassword123!", ""}, // Empty name
        };

        String[] testCaseNames = {
            "empty username",
            "invalid email format",
            "weak password",
            "empty name"
        };

        for (int i = 0; i < invalidTestCases.length; i++) {
            String[] testCase = invalidTestCases[i];
            String testCaseName = testCaseNames[i];
            
            logger.info("🔍 Testing invalid case: {}", testCaseName);

            Map<String, Object> signupRequest = new HashMap<>();
            signupRequest.put("username", testCase[0]);
            signupRequest.put("email", testCase[1]);
            signupRequest.put("password", testCase[2]);
            signupRequest.put("name", testCase[3]);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(signupRequest, createHeaders());

            ResponseEntity<Map> response = restTemplate.exchange(
                userServiceBaseUrl + "/api/auth/signup",
                HttpMethod.POST,
                request,
                Map.class
            );

            logger.info("📊 Invalid data test '{}' response status: {}", testCaseName, response.getStatusCode());
            assertTrue(response.getStatusCode().is4xxClientError(), 
                "Registration with " + testCaseName + " should fail with 4xx error");
            
            logger.info("✅ Invalid case '{}' correctly rejected", testCaseName);
        }

        logger.info("🎉 Invalid data validation tests PASSED");
    }
}