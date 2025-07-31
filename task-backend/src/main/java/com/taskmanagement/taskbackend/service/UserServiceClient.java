
package com.taskmanagement.taskbackend.service;

import com.taskmanagement.taskbackend.dto.UserResponseDto;
import com.taskmanagement.taskbackend.payload.request.LoginRequest;
import com.taskmanagement.taskbackend.payload.request.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    private final WebClient webClient;
    private final String validationEndpoint;
    private final String baseUrl;

    @Autowired
    public UserServiceClient(WebClient.Builder webClientBuilder, 
                             @Value("${user.service.url}") String userServiceUrl,
                             @Value("${user.service.validation.endpoint}") String validationEndpoint) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
        this.validationEndpoint = validationEndpoint;
        this.baseUrl = userServiceUrl;
    }

    public String validateTokenAndGetUsername(String token) {
        try {
            System.out.println("=== UserServiceClient Debug ===");
            System.out.println("Base URL: " + baseUrl);
            System.out.println("Validation endpoint: " + validationEndpoint);
            System.out.println("Full URL: " + baseUrl + validationEndpoint);
            System.out.println("Token (first 20 chars): " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
            
            String result = webClient.post()
                    .uri(validationEndpoint)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .onStatus(status -> {
                        System.out.println("4xx Error from user service - Status: " + status.value());
                        return status.is4xxClientError();
                    }, response -> {
                        System.out.println("4xx Error response received");
                        return Mono.error(new RuntimeException("Invalid token - 4xx"));
                    })
                    .onStatus(status -> {
                        System.out.println("5xx Error from user service - Status: " + status.value());
                        return status.is5xxServerError();
                    }, response -> {
                        System.out.println("5xx Error response received");
                        return Mono.error(new RuntimeException("User service unavailable - 5xx"));
                    })
                    .bodyToMono(String.class)
                    .block();
            System.out.println("Token validation SUCCESS - Username: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("EXCEPTION during token validation: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean validateToken(String token) {
        return validateTokenAndGetUsername(token) != null;
    }
    
    public Mono<Object> login(LoginRequest loginRequest) {
        return webClient.post()
                .uri("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                    Mono.error(new RuntimeException("Invalid credentials")))
                .onStatus(status -> status.is5xxServerError(), response ->
                    Mono.error(new RuntimeException("User service unavailable")))
                .bodyToMono(Object.class);
    }
    
    public Mono<Object> register(SignupRequest signupRequest) {
        return webClient.post()
                .uri("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signupRequest)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                    response.bodyToMono(String.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error))))
                .onStatus(status -> status.is5xxServerError(), response ->
                    Mono.error(new RuntimeException("User service unavailable")))
                .bodyToMono(Object.class);
    }
    
    public Mono<UserResponseDto> getUserByUsername(String username) {
        return webClient.get()
                .uri("/api/users/username/" + username)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                    Mono.error(new RuntimeException("User not found")))
                .onStatus(status -> status.is5xxServerError(), response ->
                    Mono.error(new RuntimeException("User service unavailable")))
                .bodyToMono(UserResponseDto.class);
    }
}