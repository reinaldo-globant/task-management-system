package com.taskmanagement.taskbackend.grpc;

import com.taskmanagement.taskbackend.dto.UserResponseDto;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserServiceGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    
    @Value("${app.service.token}")
    private String serviceToken;
    
    private UserServiceGrpc.UserServiceBlockingStub getAuthenticatedStub() {
        System.out.println("=== Creating Authenticated gRPC Stub ===");
        System.out.println("Service token being used: " + (serviceToken != null ? serviceToken : "NULL"));
        
        Metadata metadata = new Metadata();
        Metadata.Key<String> tokenKey = Metadata.Key.of("x-service-token", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(tokenKey, serviceToken);
        
        System.out.println("Metadata key: " + tokenKey.name());
        System.out.println("Metadata value: " + metadata.get(tokenKey));
        
        // Use the correct method signature for MetadataUtils.attachHeaders
        ClientInterceptor interceptor = MetadataUtils.newAttachHeadersInterceptor(metadata);
        return userServiceStub.withInterceptors(interceptor);
    }

    public String validateTokenAndGetUsername(String token) {
        try {
            System.out.println("=== gRPC UserService Client ===");
            System.out.println("Validating token via gRPC...");
            
            ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                    .setToken(token)
                    .build();
            
            ValidateTokenResponse response = getAuthenticatedStub().validateToken(request);
            
            System.out.println("gRPC Validation result - Valid: " + response.getValid());
            System.out.println("gRPC Validation result - Username: " + response.getUsername());
            System.out.println("gRPC Validation result - Message: " + response.getMessage());
            
            if (response.getValid()) {
                return response.getUsername();
            } else {
                System.out.println("Token validation failed: " + response.getMessage());
                return null;
            }
        } catch (Exception e) {
            System.out.println("gRPC Exception during token validation: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean validateToken(String token) {
        return validateTokenAndGetUsername(token) != null;
    }

    public UserResponseDto getUserByUsername(String username) {
        try {
            System.out.println("=== gRPC GetUserByUsername ===");
            System.out.println("Getting user: " + username);
            
            GetUserByUsernameRequest request = GetUserByUsernameRequest.newBuilder()
                    .setUsername(username)
                    .build();
            
            UserResponse response = getAuthenticatedStub().getUserByUsername(request);
            
            System.out.println("gRPC User response - ID: " + response.getId());
            System.out.println("gRPC User response - Username: " + response.getUsername());
            
            return new UserResponseDto(
                    response.getId(),
                    response.getUsername(),
                    response.getEmail(),
                    response.getName()
            );
        } catch (Exception e) {
            System.out.println("gRPC Exception during getUserByUsername: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}