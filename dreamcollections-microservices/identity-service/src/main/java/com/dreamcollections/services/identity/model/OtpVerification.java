package com.dreamcollections.services.identity.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash; // BCrypt hashed OTP

    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts = 5;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "verification_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VerificationType verificationType;

    @Column(name = "user_id")
    private Long userId; // Null for guest checkout, set after user creation

    public enum VerificationType {
        CHECKOUT, // For guest checkout
        LOGIN,    // For login
        REGISTRATION // For registration
    }

    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isMaxAttemptsReached() {
        return attempts >= maxAttempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    // Constructor for creating new OTP
    public OtpVerification(String phoneNumber, String email, String otpHash, VerificationType verificationType) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.otpHash = otpHash;
        this.verificationType = verificationType;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5); // 5 minutes TTL
        this.attempts = 0;
        this.verified = false;
    }
}
