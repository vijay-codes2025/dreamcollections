package com.dreamcollections.services.order.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections; // For empty list of authorities if none provided

public class UserPrincipal implements UserDetails { // Implement UserDetails for compatibility

    private Long id; // User ID
    private String username; // Username (subject of JWT)
    private Collection<? extends GrantedAuthority> authorities;
    // We don't store password here as it's for an authenticated principal from JWT

    public UserPrincipal(Long id, String username, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.authorities = authorities != null ? authorities : Collections.emptyList();
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // Not applicable for JWT principal
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Implement other UserDetails methods
    @Override
    public boolean isAccountNonExpired() {
        return true; // Assuming JWT validation covers this
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Assuming JWT validation covers this
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Assuming JWT validation covers this
    }

    @Override
    public boolean isEnabled() {
        return true; // Assuming JWT validation covers this
    }
}
