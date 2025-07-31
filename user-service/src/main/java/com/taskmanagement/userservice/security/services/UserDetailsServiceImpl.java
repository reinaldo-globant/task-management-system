package com.taskmanagement.userservice.security.services;

import com.taskmanagement.userservice.model.User;
import com.taskmanagement.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔧 UserDetailsService: loadUserByUsername called with: '" + username + "'");
        
        // Handle service authentication principals - these are NOT real users in the database
        if ("SERVICE_AUTHENTICATED".equals(username)) {
            System.out.println("✅ UserDetailsService: HANDLING service principal, skipping DB lookup");
            // Return a dummy UserDetails for service authentication
            // This won't be used for actual authorization, just to satisfy Spring Security
            return org.springframework.security.core.userdetails.User.builder()
                    .username(username)
                    .password("") // No password needed for service auth
                    .authorities("ROLE_SERVICE")
                    .build();
        }
        
        // Handle real user authentication - lookup in database
        System.out.println("👤 UserDetailsService: Looking up REAL user in database: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }
}