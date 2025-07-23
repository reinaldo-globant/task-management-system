package com.taskmanagement.taskbackend.controller;

import com.taskmanagement.taskbackend.model.User;
import com.taskmanagement.taskbackend.payload.request.LoginRequest;
import com.taskmanagement.taskbackend.payload.request.SignupRequest;
import com.taskmanagement.taskbackend.payload.response.JwtResponse;
import com.taskmanagement.taskbackend.payload.response.MessageResponse;
import com.taskmanagement.taskbackend.repository.UserRepository;
import com.taskmanagement.taskbackend.security.jwt.JwtUtils;
import com.taskmanagement.taskbackend.security.services.UserDetailsImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/signin")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                                                 userDetails.getId(),
                                                 userDetails.getUsername(),
                                                 userDetails.getEmail(),
                                                 userDetails.getFullName()));
    }

    @Operation(summary = "Register user", description = "Registers a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Username or email already in use", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/signup")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                             signUpRequest.getEmail(),
                             encoder.encode(signUpRequest.getPassword()),
                             signUpRequest.getFullName());

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}