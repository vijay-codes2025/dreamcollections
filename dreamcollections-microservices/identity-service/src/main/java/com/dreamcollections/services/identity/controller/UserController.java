package com.dreamcollections.services.identity.controller;

import com.dreamcollections.services.identity.model.User;
import com.dreamcollections.services.identity.payload.response.UserProfileResponse; // New DTO
import com.dreamcollections.services.identity.repository.UserRepository;
import com.dreamcollections.services.identity.exception.ResourceNotFoundException; // Custom exception

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users") // Path within this service, API gateway will map /api/identity/users to this
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // Any authenticated user can access their own profile
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        log.info("Fetching profile for user: {}", currentUsername);

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> {
                    log.error("Authenticated user {} not found in database.", currentUsername);
                    return new ResourceNotFoundException("User", "username", currentUsername);
                });

        UserProfileResponse userProfile = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name() // Send role as string
        );
        log.debug("Profile data for {}: {}", currentUsername, userProfile);
        return ResponseEntity.ok(userProfile);
    }

    // Example: Admin endpoint to get any user's profile by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable Long id) {
        log.info("Admin fetching profile for user ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        UserProfileResponse userProfile = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
        return ResponseEntity.ok(userProfile);
    }
}
