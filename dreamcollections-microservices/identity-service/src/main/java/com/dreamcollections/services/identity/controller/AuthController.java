package com.dreamcollections.services.identity.controller;

import com.dreamcollections.services.identity.model.User;
import com.dreamcollections.services.identity.model.UserRole;
import com.dreamcollections.services.identity.payload.request.LoginRequest;
import com.dreamcollections.services.identity.payload.request.SignupRequest;
import com.dreamcollections.services.identity.payload.response.JwtResponse;
import com.dreamcollections.services.identity.payload.response.MessageResponse;
import com.dreamcollections.services.identity.repository.UserRepository;
import com.dreamcollections.services.identity.security.JwtUtils;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher; // For publishing events
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600) // Configure this more strictly in API Gateway
@RestController
@RequestMapping("/auth") // Path within this service, API gateway will map /api/identity/auth to this
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // @Autowired
    // ApplicationEventPublisher eventPublisher; // For publishing UserCreatedEvent later

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Attempting to sign in user: {}", loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Principal is now UserPrincipal which contains id, username, email
        com.dreamcollections.services.identity.security.UserPrincipal userPrincipal = (com.dreamcollections.services.identity.security.UserPrincipal) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("User {} signed in successfully.", userPrincipal.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwt,
                                                 userPrincipal.getId(),
                                                 userPrincipal.getUsername(),
                                                 userPrincipal.getEmail(), // Get email from UserPrincipal
                                                 roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        log.info("Attempting to register new user: {}", signUpRequest.getUsername());
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            log.warn("Username {} already taken.", signUpRequest.getUsername());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!", false));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            log.warn("Email {} already in use.", signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!", false));
        }

        User user = new User(signUpRequest.getUsername(),
                             encoder.encode(signUpRequest.getPassword()),
                             signUpRequest.getEmail(),
                             signUpRequest.getFirstName(),
                             signUpRequest.getLastName(),
                             null); // Role set below

        String strRole = signUpRequest.getRole();
        UserRole roleEnum;

        if (strRole == null || strRole.trim().isEmpty()) {
            roleEnum = UserRole.ROLE_CUSTOMER;
        } else {
            try {
                roleEnum = UserRole.valueOf("ROLE_" + strRole.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role specified during signup: {}", strRole);
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Role '" + strRole + "' is not valid.", false));
            }
        }
        user.setRole(roleEnum);
        User savedUser = userRepository.save(user);
        log.info("User {} registered successfully with ID {}.", savedUser.getUsername(), savedUser.getId());

        // TODO - Phase 2: Publish UserCreatedEvent or make a call to CartService
        // Example: eventPublisher.publishEvent(new UserCreatedEvent(this, savedUser.getId(), savedUser.getUsername()));
        // For now, cart creation is deferred.

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
