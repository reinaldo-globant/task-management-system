package com.taskmanagement.taskbackend.security.services;

import com.taskmanagement.taskbackend.dto.UserResponseDto;
import com.taskmanagement.taskbackend.service.InternalUserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private InternalUserServiceClient internalUserServiceClient;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserResponseDto user = internalUserServiceClient.getUserDetailsByUsername(username).block();
            if (user == null) {
                throw new UsernameNotFoundException("User Not Found with username: " + username);
            }
            return UserDetailsImpl.build(user);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User Not Found with username: " + username, e);
        }
    }
}