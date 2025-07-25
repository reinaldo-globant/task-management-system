package com.taskmanagement.userservice.controller;

import com.taskmanagement.userservice.model.User;
import com.taskmanagement.userservice.payload.response.MessageResponse;
import com.taskmanagement.userservice.repository.UserRepository;
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
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.substring(7); // Remove "Bearer " prefix
            boolean isValid = jwtUtils.validateJwtToken(jwt);
            
            if (isValid) {
                // Get username from token and verify the user exists
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                if (userRepository.findByUsername(username).isPresent()) {
                    return ResponseEntity.ok(new MessageResponse("Token is valid"));
                }
            }
            return ResponseEntity.status(401).body(new MessageResponse("Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new MessageResponse("Invalid token: " + e.getMessage()));
        }
    }
}