package com.taskmanagement.userservice.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmanagement.userservice.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails, OAuth2User {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private String name;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserDetailsImpl(Long id, String username, String email, String name, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
        this.authorities = authorities;
    }

    public UserDetailsImpl(Long id, String username, String email, String name, String password,
                           Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getPassword(),
                authorities);
    }

    public static UserDetailsImpl create(User user, Map<String, Object> attributes) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getPassword(),
                authorities,
                attributes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User interface methods
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}