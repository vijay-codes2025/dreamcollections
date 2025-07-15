package com.dreamcollections.services.identity.controller;

import com.dreamcollections.services.identity.model.OtpVerification;
import com.dreamcollections.services.identity.model.User;
import com.dreamcollections.services.identity.model.UserRole;
import com.dreamcollections.services.identity.payload.request.OtpRequest;
import com.dreamcollections.services.identity.payload.request.OtpVerifyRequest;
import com.dreamcollections.services.identity.payload.response.JwtResponse;
import com.dreamcollections.services.identity.payload.response.MessageResponse;
import com.dreamcollections.services.identity.payload.response.OtpResponse;
import com.dreamcollections.services.identity.repository.UserRepository;
import com.dreamcollections.services.identity.security.JwtUtils;
import com.dreamcollections.services.identity.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth/otp")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class OtpController {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Send OTP for login/checkout
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpRequest otpRequest) {
        try {
            log.info("OTP send request for phone: {}, email: {}", 
                    maskPhone(otpRequest.getPhoneNumber()), 
                    maskEmail(otpRequest.getEmail()));

            OtpVerification.VerificationType type = OtpVerification.VerificationType.valueOf(
                    otpRequest.getType().toUpperCase());

            String result = otpService.generateOtp(
                    otpRequest.getPhoneNumber(), 
                    otpRequest.getEmail(), 
                    type);

            OtpResponse response = new OtpResponse(
                    true, 
                    result,
                    maskPhone(otpRequest.getPhoneNumber()),
                    maskEmail(otpRequest.getEmail()),
                    300 // 5 minutes in seconds
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error sending OTP: ", e);
            return ResponseEntity.badRequest()
                    .body(new OtpResponse(false, e.getMessage()));
        }
    }

    /**
     * Verify OTP and login/create user
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerifyRequest verifyRequest) {
        try {
            log.info("OTP verify request for phone: {}, email: {}", 
                    maskPhone(verifyRequest.getPhoneNumber()), 
                    maskEmail(verifyRequest.getEmail()));

            OtpService.OtpVerificationResult result = otpService.verifyOtp(
                    verifyRequest.getPhoneNumber(),
                    verifyRequest.getEmail(),
                    verifyRequest.getOtp()
            );

            if (!result.isSuccess()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse(result.getMessage(), false));
            }

            // OTP verified successfully, now handle user creation/login
            User user = findOrCreateUser(verifyRequest.getPhoneNumber(), verifyRequest.getEmail());

            // Generate JWT tokens
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
            );

            String accessToken = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);

            List<String> roles = Collections.singletonList(user.getRole().name());

            JwtResponse jwtResponse = new JwtResponse(
                    accessToken,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    roles
            );

            // Add refresh token to response
            jwtResponse.setRefreshToken(refreshToken);

            log.info("OTP verification successful for user: {}", user.getUsername());
            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            log.error("Error verifying OTP: ", e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("OTP verification failed: " + e.getMessage(), false));
        }
    }

    /**
     * Resend OTP
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendOtp(@Valid @RequestBody OtpRequest otpRequest) {
        try {
            log.info("OTP resend request for phone: {}, email: {}", 
                    maskPhone(otpRequest.getPhoneNumber()), 
                    maskEmail(otpRequest.getEmail()));

            String result = otpService.resendOtp(
                    otpRequest.getPhoneNumber(), 
                    otpRequest.getEmail());

            OtpResponse response = new OtpResponse(
                    true, 
                    result,
                    maskPhone(otpRequest.getPhoneNumber()),
                    maskEmail(otpRequest.getEmail()),
                    300 // 5 minutes in seconds
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error resending OTP: ", e);
            return ResponseEntity.badRequest()
                    .body(new OtpResponse(false, e.getMessage()));
        }
    }

    /**
     * Find existing user or create new one
     */
    private User findOrCreateUser(String phoneNumber, String email) {
        // Try to find existing user by phone or email
        Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);
        
        if (existingUser.isEmpty() && email != null) {
            existingUser = userRepository.findByEmail(email);
        }

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update phone number if it was missing
            if (user.getPhoneNumber() == null && phoneNumber != null) {
                user.setPhoneNumber(phoneNumber);
                userRepository.save(user);
            }
            return user;
        }

        // Create new user
        String username = generateUsername(phoneNumber, email);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode("OTP_USER")); // Placeholder password
        newUser.setRole(UserRole.ROLE_CUSTOMER);
        newUser.setFirstName("Customer");
        newUser.setLastName("");

        return userRepository.save(newUser);
    }

    /**
     * Generate username from phone/email
     */
    private String generateUsername(String phoneNumber, String email) {
        if (phoneNumber != null) {
            // Use last 10 digits of phone number
            String digits = phoneNumber.replaceAll("[^0-9]", "");
            if (digits.length() >= 10) {
                return "user_" + digits.substring(digits.length() - 10);
            }
        }
        
        if (email != null) {
            return email.split("@")[0];
        }
        
        return "user_" + System.currentTimeMillis();
    }

    /**
     * Mask phone number for logging
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return phone;
        return phone.substring(0, phone.length() - 4) + "••••";
    }

    /**
     * Mask email for logging
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) return email;
        return username.substring(0, 2) + "•••@" + domain;
    }
}
