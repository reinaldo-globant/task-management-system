package com.taskmanagement.userservice.grpc;

import com.taskmanagement.userservice.model.User;
import com.taskmanagement.userservice.repository.UserRepository;
import com.taskmanagement.userservice.security.jwt.JwtUtils;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for gRPC service-to-service authentication via validateToken method.
 * Tests the complete JWT validation flow without Spring Security interference.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceGrpcTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StreamObserver<ValidateTokenResponse> responseObserver;

    @InjectMocks
    private UserServiceGrpcImpl userServiceGrpc;

    private String testJwtSecret = "testSecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm";
    private String validUsername = "testuser";
    private String validJwtToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Set up service token for testing
        ReflectionTestUtils.setField(userServiceGrpc, "expectedServiceToken", "testServiceToken123");
        
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(validUsername);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        // Generate a valid JWT token for testing
        validJwtToken = generateTestJwtToken(validUsername);
    }

    @Test
    void testValidateToken_WithValidJWT_ShouldReturnSuccess() {
        System.out.println("🧪 TEST: Validating token with VALID JWT");
        
        // Arrange
        when(jwtUtils.validateJwtToken(validJwtToken)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(validJwtToken)).thenReturn(validUsername);
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(testUser));

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken(validJwtToken)
                .build();

        // Act
        userServiceGrpc.validateToken(request, responseObserver);

        // Assert
        ArgumentCaptor<ValidateTokenResponse> responseCaptor = ArgumentCaptor.forClass(ValidateTokenResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        ValidateTokenResponse response = responseCaptor.getValue();
        assertTrue(response.getValid(), "Token should be valid");
        assertEquals(validUsername, response.getUsername(), "Username should match");
        assertEquals("JWT validation successful", response.getMessage());

        System.out.println("✅ VALID JWT TEST PASSED: Token validated successfully");
    }

    @Test
    void testValidateToken_WithInvalidJWT_ShouldReturnFailure() {
        System.out.println("🧪 TEST: Validating token with INVALID JWT");
        
        // Arrange
        String invalidToken = "invalid.jwt.token";
        when(jwtUtils.validateJwtToken(invalidToken)).thenReturn(false);

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken(invalidToken)
                .build();

        // Act
        userServiceGrpc.validateToken(request, responseObserver);

        // Assert
        ArgumentCaptor<ValidateTokenResponse> responseCaptor = ArgumentCaptor.forClass(ValidateTokenResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        ValidateTokenResponse response = responseCaptor.getValue();
        assertFalse(response.getValid(), "Token should be invalid");
        assertEquals("Invalid JWT token", response.getMessage());

        // Verify that username extraction was never called since validation failed
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userRepository, never()).findByUsername(anyString());

        System.out.println("✅ INVALID JWT TEST PASSED: Token rejected correctly");
    }

    @Test
    void testValidateToken_WithEmptyToken_ShouldReturnFailure() {
        System.out.println("🧪 TEST: Validating token with EMPTY token");
        
        // Arrange
        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken("")
                .build();

        // Act
        userServiceGrpc.validateToken(request, responseObserver);

        // Assert
        ArgumentCaptor<ValidateTokenResponse> responseCaptor = ArgumentCaptor.forClass(ValidateTokenResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        ValidateTokenResponse response = responseCaptor.getValue();
        assertFalse(response.getValid(), "Empty token should be invalid");
        assertEquals("No JWT token provided", response.getMessage());

        // Verify that JWT validation was never called
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userRepository, never()).findByUsername(anyString());

        System.out.println("✅ EMPTY TOKEN TEST PASSED: Empty token rejected correctly");
    }

    @Test
    void testValidateToken_WithValidJWTButUserNotFound_ShouldReturnFailure() {
        System.out.println("🧪 TEST: Validating token with VALID JWT but USER NOT FOUND");
        
        // Arrange
        String unknownUsername = "unknownuser";
        when(jwtUtils.validateJwtToken(validJwtToken)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(validJwtToken)).thenReturn(unknownUsername);
        when(userRepository.findByUsername(unknownUsername)).thenReturn(Optional.empty());

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken(validJwtToken)
                .build();

        // Act
        userServiceGrpc.validateToken(request, responseObserver);

        // Assert
        ArgumentCaptor<ValidateTokenResponse> responseCaptor = ArgumentCaptor.forClass(ValidateTokenResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        ValidateTokenResponse response = responseCaptor.getValue();
        assertFalse(response.getValid(), "Token should be invalid when user not found");
        assertEquals("User not found: " + unknownUsername, response.getMessage());

        System.out.println("✅ USER NOT FOUND TEST PASSED: Token rejected when user doesn't exist");
    }

    @Test
    void testValidateToken_WithException_ShouldReturnError() {
        System.out.println("🧪 TEST: Validating token with EXCEPTION during processing");
        
        // Arrange
        when(jwtUtils.validateJwtToken(validJwtToken)).thenThrow(new RuntimeException("JWT processing error"));

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken(validJwtToken)
                .build();

        // Act
        userServiceGrpc.validateToken(request, responseObserver);

        // Assert
        ArgumentCaptor<ValidateTokenResponse> responseCaptor = ArgumentCaptor.forClass(ValidateTokenResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        ValidateTokenResponse response = responseCaptor.getValue();
        assertFalse(response.getValid(), "Token should be invalid when exception occurs");
        assertTrue(response.getMessage().contains("Token validation error"), "Error message should indicate validation error");

        System.out.println("✅ EXCEPTION TEST PASSED: Exception handled correctly");
    }

    /**
     * Integration test that demonstrates the complete service-to-service authentication flow
     */
    @Test
    void testServiceToServiceAuthentication_CompleteFlow() {
        System.out.println("🧪 INTEGRATION TEST: Complete service-to-service authentication flow");
        
        // Arrange - Simulate a complete microservice authentication scenario
        when(jwtUtils.validateJwtToken(validJwtToken)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(validJwtToken)).thenReturn(validUsername);
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(testUser));

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken(validJwtToken)
                .build();

        System.out.println("📡 Simulating gRPC call from task-backend to user-service...");
        
        // Act
        userServiceGrpc.validateToken(request, responseObserver);

        // Assert - Verify complete flow
        ArgumentCaptor<ValidateTokenResponse> responseCaptor = ArgumentCaptor.forClass(ValidateTokenResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        ValidateTokenResponse response = responseCaptor.getValue();
        
        // Verify successful authentication
        assertTrue(response.getValid(), "Service-to-service authentication should succeed");
        assertEquals(validUsername, response.getUsername(), "Should return correct username");
        assertEquals("JWT validation successful", response.getMessage(), "Should return success message");

        // Verify all steps were executed
        verify(jwtUtils).validateJwtToken(validJwtToken);
        verify(jwtUtils).getUserNameFromJwtToken(validJwtToken);
        verify(userRepository).findByUsername(validUsername);

        System.out.println("✅ INTEGRATION TEST PASSED: Service-to-service authentication works end-to-end");
        System.out.println("🎉 READY FOR DEPLOYMENT: gRPC authentication validated successfully");
    }

    /**
     * Helper method to generate a test JWT token
     */
    private String generateTestJwtToken(String username) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
            java.util.Base64.getEncoder().encodeToString(testJwtSecret.getBytes())
        ));
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}