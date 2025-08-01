package com.taskmanagement.userservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    @Value("${server.port:8081}")
    private String serverPort;

    @GetMapping("/providers")
    public ResponseEntity<Map<String, Object>> getAvailableProviders() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> providers = new HashMap<>();
        
        // Only add providers that are properly configured
        // Check if OAuth2 client credentials are configured (not default values)
        String googleClientId = System.getenv("GOOGLE_CLIENT_ID");
        String githubClientId = System.getenv("GITHUB_CLIENT_ID");
        String microsoftClientId = System.getenv("MICROSOFT_CLIENT_ID");
        
        if (googleClientId != null && !googleClientId.startsWith("your-google-client-id")) {
            providers.put("google", "http://localhost:" + serverPort + "/oauth2/authorization/google");
        }
        
        if (githubClientId != null && !githubClientId.startsWith("your-github-client-id")) {
            providers.put("github", "http://localhost:" + serverPort + "/oauth2/authorization/github");
        }
        
        if (microsoftClientId != null && !microsoftClientId.startsWith("your-microsoft-client-id")) {
            providers.put("microsoft", "http://localhost:" + serverPort + "/oauth2/authorization/microsoft");
        }
        
        response.put("providers", providers);
        
        if (providers.isEmpty()) {
            response.put("message", "OAuth2 providers not configured. Please set up environment variables as described in SSO_SETUP.md");
            response.put("configured", false);
        } else {
            response.put("message", "Available OAuth2 providers");
            response.put("configured", true);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/redirect")
    public ResponseEntity<Map<String, Object>> handleRedirect(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String error) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (error != null) {
            response.put("success", false);
            response.put("error", error);
            response.put("message", "OAuth2 authentication failed");
        } else if (token != null && username != null) {
            response.put("success", true);
            response.put("token", token);
            response.put("username", username);
            response.put("message", "OAuth2 authentication successful");
        } else {
            response.put("success", false);
            response.put("error", "Missing authentication data");
            response.put("message", "OAuth2 authentication incomplete");
        }
        
        return ResponseEntity.ok(response);
    }
}