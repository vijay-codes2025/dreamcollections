package com.dreamcollections.services.identity.service;

import com.dreamcollections.services.identity.model.User;
import com.dreamcollections.services.identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.dreamcollections.services.identity.security.UserPrincipal; // Import custom UserPrincipal
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // Try to find user by username, email, or phone number
        User user = userRepository.findByUsername(loginId)
                .or(() -> userRepository.findByEmail(loginId))
                .or(() -> userRepository.findByPhoneNumber(loginId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login ID: " + loginId));

        Set<GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(user.getRole().name())
        );

        // Return custom UserPrincipal
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
