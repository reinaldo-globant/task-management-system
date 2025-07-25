package com.taskmanagement.userservice.controller;

import com.taskmanagement.userservice.payload.response.MessageResponse;
import com.taskmanagement.userservice.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class TokenValidationController {

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.substring(7); // Remove "Bearer " prefix
            boolean isValid = jwtUtils.validateJwtToken(jwt);
            
            if (isValid) {
                return ResponseEntity.ok(new MessageResponse("Token is valid"));
            } else {
                return ResponseEntity.status(401).body(new MessageResponse("Invalid token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new MessageResponse("Invalid token: " + e.getMessage()));
        }
    }
}