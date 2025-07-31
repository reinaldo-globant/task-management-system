package com.taskmanagement.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class InternalSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain internalFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/internal/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> 
                auth.anyRequest().permitAll()
            );
        return http.build();
    }
}