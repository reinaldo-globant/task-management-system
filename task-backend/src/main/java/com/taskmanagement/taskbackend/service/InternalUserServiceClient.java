package com.taskmanagement.taskbackend.service;

import java.util.Map;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.taskmanagement.taskbackend.dto.UserResponseDto;

import reactor.core.publisher.Mono;

@Service
public class InternalUserServiceClient {

    private final WebClient webClient;
    
    @Value("${app.service.token}")
    private String internalServiceToken;

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(InternalUserServiceClient.class);

    @Autowired
    public InternalUserServiceClient(WebClient.Builder webClientBuilder, 
                                   @Value("${user.service.url}") String userServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

    public Mono<Long> getUserIdByUsername(String username) {
        log.info("Fetching username: {}",username);
        return webClient.post()
                .uri("/internal/users/user-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("username",username))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                    Mono.error(new RuntimeException("User not found or unauthorized for username: ")))
                .onStatus(status -> status.is5xxServerError(), response ->
                    Mono.error(new RuntimeException("User service unavailable")))
                .bodyToMono(Long.class);
    }

    public Mono<UserResponseDto> getUserDetailsByUsername(String username) {
        log.info("Fetching user details for username: {}", username);
        return webClient.post()
                .uri("/internal/users/user-details")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("username", username))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                    Mono.error(new RuntimeException("User not found or unauthorized for username: " + username)))
                .onStatus(status -> status.is5xxServerError(), response ->
                    Mono.error(new RuntimeException("User service unavailable")))
                .bodyToMono(UserResponseDto.class);
    }
}