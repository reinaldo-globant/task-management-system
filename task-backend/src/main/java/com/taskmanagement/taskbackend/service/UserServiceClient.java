package com.taskmanagement.taskbackend.service;

import com.taskmanagement.taskbackend.payload.request.LoginRequest;
import com.taskmanagement.taskbackend.payload.request.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    public boolean validateToken(String token) {
        try {
            return webClient.post()
                    .uri(validationEndpoint)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> 
                        Mono.error(new RuntimeException("Invalid token")))
                    .onStatus(status -> status.is5xxServerError(), response -> 
                        Mono.error(new RuntimeException("User service unavailable")))
                    .bodyToMono(String.class)
                    .map(response -> true)
                    .onErrorReturn(false)
                    .block();
        } catch (Exception e) {
            return false;
        }
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
}