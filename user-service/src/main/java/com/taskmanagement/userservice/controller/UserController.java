package com.taskmanagement.userservice.controller;

import com.taskmanagement.userservice.model.User;
import com.taskmanagement.userservice.payload.response.MessageResponse;
import com.taskmanagement.userservice.repository.UserRepository;
import com.taskmanagement.userservice.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            // Don't return the password
            User userResponse = user.get();
            userResponse.setPassword("");
            return ResponseEntity.ok(userResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.substring(7); // Remove "Bearer " prefix
            boolean isValid = jwtUtils.validateJwtToken(jwt);
            if (isValid) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                Optional<User> user = userRepository.findByUsername(username);
                if (user.isPresent()) {
                    return ResponseEntity.ok().body(new MessageResponse("Token is valid"));
                }
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error validating token"));
        }
    }
}