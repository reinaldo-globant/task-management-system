package com.taskmanagement.userservice.grpc;

import com.taskmanagement.userservice.model.User;
import com.taskmanagement.userservice.repository.UserRepository;
import com.taskmanagement.userservice.security.jwt.JwtUtils;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@GrpcService
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.service.token}")
    private String expectedServiceToken;

    @Override
    public void validateToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
        System.out.println("=== validateToken - COMPLETELY INDEPENDENT FROM SPRING SECURITY ===");
        
        try {
            // STEP 1: Manual service token validation (bypass all Spring Security)
            // For now, skip service token validation since gRPC config allows all
            // In production, you would validate the service token from headers manually
            System.out.println("✅ Service authentication bypassed (permitAll configured)");
            
            // STEP 2: EXTRACT JWT from request payload (completely independent)
            String jwtToken = request.getToken();
            System.out.println("🔧 Extracted JWT from request payload (length: " + 
                             (jwtToken != null ? jwtToken.length() : 0) + ")");
            
            if (jwtToken == null || jwtToken.trim().isEmpty()) {
                System.out.println("❌ No JWT token in request payload");
                ValidateTokenResponse response = ValidateTokenResponse.newBuilder()
                        .setValid(false)
                        .setMessage("No JWT token provided")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            // STEP 3: VALIDATE JWT using JwtUtils (completely independent)
            System.out.println("🔧 Validating JWT using JwtUtils...");
            boolean isValidJwt = jwtUtils.validateJwtToken(jwtToken);
            System.out.println("🔐 JWT validation result: " + (isValidJwt ? "✅ VALID" : "❌ INVALID"));
            
            if (!isValidJwt) {
                System.out.println("❌ JWT validation failed");
                ValidateTokenResponse response = ValidateTokenResponse.newBuilder()
                        .setValid(false)
                        .setMessage("Invalid JWT token")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            // STEP 4: EXTRACT username from JWT (completely independent)
            System.out.println("🔧 Extracting username from JWT...");
            String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            System.out.println("👤 Extracted username: '" + username + "'");
            
            // STEP 5: Verify user exists in database (completely independent)
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (!userOptional.isPresent()) {
                System.out.println("❌ User not found in database: " + username);
                ValidateTokenResponse response = ValidateTokenResponse.newBuilder()
                        .setValid(false)
                        .setMessage("User not found: " + username)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            // STEP 6: RETURN successful response with username (completely independent)
            System.out.println("✅ User found in database: " + username);
            System.out.println("🎉 RETURNING SUCCESSFUL RESPONSE - NO SPRING SECURITY INVOLVED");
            ValidateTokenResponse response = ValidateTokenResponse.newBuilder()
                    .setValid(true)
                    .setUsername(username)
                    .setMessage("JWT validation successful")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            System.out.println("✅ validateToken() EXECUTED SUCCESSFULLY - METHOD COMPLETE");
            
        } catch (Exception e) {
            System.err.println("💥 Exception in validateToken: " + e.getMessage());
            e.printStackTrace();
            
            ValidateTokenResponse errorResponse = ValidateTokenResponse.newBuilder()
                    .setValid(false)
                    .setMessage("Token validation error: " + e.getMessage())
                    .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUserByUsername(GetUserByUsernameRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String username = request.getUsername();
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserResponse response = UserResponse.newBuilder()
                        .setId(user.getId())
                        .setUsername(user.getUsername())
                        .setEmail(user.getEmail())
                        .setName(user.getName())
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(new RuntimeException("User not found: " + username));
            }
        } catch (Exception e) {
            responseObserver.onError(new RuntimeException("Error getting user: " + e.getMessage()));
        }
    }
}