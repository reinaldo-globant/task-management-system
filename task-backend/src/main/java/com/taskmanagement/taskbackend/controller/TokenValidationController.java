package com.taskmanagement.taskbackend.controller;

import com.taskmanagement.taskbackend.service.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/validation")
public class TokenValidationController {

    @Autowired
    private UserServiceClient userServiceClient;

    @PostMapping("/token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        boolean isValid = userServiceClient.validateToken(token);
        
        Map<String, Object> response = new HashMap<>();
        if (isValid) {
            response.put("valid", true);
            response.put("message", "Token is valid");
            return ResponseEntity.ok(response);
        } else {
            response.put("valid", false);
            response.put("message", "Invalid token");
            return ResponseEntity.status(401).body(response);
        }
    }
}