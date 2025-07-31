package com.taskmanagement.userservice.controller;

import com.taskmanagement.userservice.dto.UsernameRequest;
import com.taskmanagement.userservice.model.User;
import com.taskmanagement.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private static final Logger log = LoggerFactory.getLogger(InternalUserController.class);

    @Autowired
    private UserRepository userRepository;

    @Value("${app.service.token}")
    private String internalServiceToken;

    @PostMapping("/user-id")
    public ResponseEntity<Long> getUserIdByUsername(@RequestBody UsernameRequest request) {
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Optional<User> userOptional = userRepository.findByUsername(request.getUsername().trim());
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOptional.get();
            Long userId = user.getId();
            
            // Fallback for Lombok issues
            if (userId == null) {
                try {
                    java.lang.reflect.Field idField = User.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    userId = (Long) idField.get(user);
                } catch (Exception e) {
                    log.error("Error accessing user ID: {}", e.getMessage());
                    return ResponseEntity.internalServerError().build();
                }
            }
            
            return ResponseEntity.ok(userId);
            
        } catch (Exception e) {
            log.error("Error processing user ID request: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/user-details")
    public ResponseEntity<User> getUserDetailsByUsername(@RequestBody UsernameRequest request) {
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Optional<User> userOptional = userRepository.findByUsername(request.getUsername().trim());
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(userOptional.get());
            
        } catch (Exception e) {
            log.error("Error processing user details request: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Internal endpoint working");
    }
}