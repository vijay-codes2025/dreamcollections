package com.dreamcollections.services.identity.service;

import com.dreamcollections.services.identity.model.OtpVerification;
import com.dreamcollections.services.identity.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4); // Cost 4 for OTPs
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int OTP_LENGTH = 6;
    private static final int MAX_ATTEMPTS_PER_15_MIN = 5;

    /**
     * Generate and send OTP for phone number
     */
    @Transactional
    public String generateOtp(String phoneNumber, String email, OtpVerification.VerificationType type) {
        log.info("Generating OTP for phone: {}, email: {}, type: {}", 
                maskPhone(phoneNumber), maskEmail(email), type);

        // Check rate limiting
        if (isRateLimited(phoneNumber)) {
            throw new RuntimeException("Too many OTP requests. Please try again later.");
        }

        // Generate 6-digit OTP
        String otp = generateRandomOtp();
        String hashedOtp = passwordEncoder.encode(otp);

        // Invalidate any existing active OTPs for this phone/email
        invalidateExistingOtps(phoneNumber, email);

        // Create new OTP record
        OtpVerification otpVerification = new OtpVerification(phoneNumber, email, hashedOtp, type);
        otpRepository.save(otpVerification);

        // In a real application, you would send the OTP via SMS/Email here
        sendOtpViaSms(phoneNumber, otp);
        if (email != null && !email.isEmpty()) {
            sendOtpViaEmail(email, otp);
        }

        log.info("OTP generated and sent successfully for phone: {}", maskPhone(phoneNumber));
        return "OTP sent successfully to " + maskPhone(phoneNumber) + 
               (email != null ? " and " + maskEmail(email) : "");
    }

    /**
     * Verify OTP
     */
    @Transactional
    public OtpVerificationResult verifyOtp(String phoneNumber, String email, String otp) {
        log.info("Verifying OTP for phone: {}, email: {}", maskPhone(phoneNumber), maskEmail(email));

        Optional<OtpVerification> otpRecord = otpRepository.findActiveOtpByPhoneOrEmail(
                phoneNumber, email, LocalDateTime.now());

        if (otpRecord.isEmpty()) {
            log.warn("No active OTP found for phone: {}, email: {}", maskPhone(phoneNumber), maskEmail(email));
            return new OtpVerificationResult(false, "No active OTP found or OTP has expired");
        }

        OtpVerification verification = otpRecord.get();

        // Check if max attempts reached
        if (verification.isMaxAttemptsReached()) {
            log.warn("Max attempts reached for OTP verification: {}", verification.getId());
            return new OtpVerificationResult(false, "Maximum verification attempts exceeded");
        }

        // Check if expired
        if (verification.isExpired()) {
            log.warn("OTP expired for verification: {}", verification.getId());
            return new OtpVerificationResult(false, "OTP has expired");
        }

        // Verify OTP
        verification.incrementAttempts();
        otpRepository.save(verification);

        if (passwordEncoder.matches(otp, verification.getOtpHash())) {
            // Mark as verified
            verification.setVerified(true);
            otpRepository.save(verification);
            
            log.info("OTP verified successfully for phone: {}", maskPhone(phoneNumber));
            return new OtpVerificationResult(true, "OTP verified successfully", verification);
        } else {
            log.warn("Invalid OTP provided for phone: {}", maskPhone(phoneNumber));
            return new OtpVerificationResult(false, "Invalid OTP");
        }
    }

    /**
     * Resend OTP
     */
    @Transactional
    public String resendOtp(String phoneNumber, String email) {
        log.info("Resending OTP for phone: {}, email: {}", maskPhone(phoneNumber), maskEmail(email));

        // Find existing OTP to determine type
        Optional<OtpVerification> existingOtp = otpRepository.findActiveOtpByPhoneOrEmail(
                phoneNumber, email, LocalDateTime.now());

        OtpVerification.VerificationType type = existingOtp
                .map(OtpVerification::getVerificationType)
                .orElse(OtpVerification.VerificationType.LOGIN);

        return generateOtp(phoneNumber, email, type);
    }

    /**
     * Check if phone number is rate limited
     */
    private boolean isRateLimited(String phoneNumber) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(15);
        Long recentAttempts = otpRepository.countRecentAttemptsByPhone(phoneNumber, since);
        return recentAttempts >= MAX_ATTEMPTS_PER_15_MIN;
    }

    /**
     * Invalidate existing active OTPs
     */
    private void invalidateExistingOtps(String phoneNumber, String email) {
        Optional<OtpVerification> existingOtp = otpRepository.findActiveOtpByPhoneOrEmail(
                phoneNumber, email, LocalDateTime.now());
        
        if (existingOtp.isPresent()) {
            OtpVerification otp = existingOtp.get();
            otp.setVerified(true); // Mark as used to invalidate
            otpRepository.save(otp);
        }
    }

    /**
     * Generate random 6-digit OTP
     */
    private String generateRandomOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Send OTP via SMS (mock implementation)
     */
    private void sendOtpViaSms(String phoneNumber, String otp) {
        // In production, integrate with SMS service like Twilio, AWS SNS, etc.
        log.info("SMS OTP sent to {}: {}", maskPhone(phoneNumber), otp);
        // For demo purposes, we'll just log it
        System.out.println("📱 SMS OTP for " + maskPhone(phoneNumber) + ": " + otp);
    }

    /**
     * Send OTP via Email (mock implementation)
     */
    private void sendOtpViaEmail(String email, String otp) {
        // In production, integrate with email service like SendGrid, AWS SES, etc.
        log.info("Email OTP sent to {}: {}", maskEmail(email), otp);
        // For demo purposes, we'll just log it
        System.out.println("📧 Email OTP for " + maskEmail(email) + ": " + otp);
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

    /**
     * Clean up expired OTPs (scheduled task)
     */
    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
        log.info("Cleaned up expired OTPs");
    }

    /**
     * OTP Verification Result
     */
    public static class OtpVerificationResult {
        private final boolean success;
        private final String message;
        private final OtpVerification otpVerification;

        public OtpVerificationResult(boolean success, String message) {
            this(success, message, null);
        }

        public OtpVerificationResult(boolean success, String message, OtpVerification otpVerification) {
            this.success = success;
            this.message = message;
            this.otpVerification = otpVerification;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public OtpVerification getOtpVerification() { return otpVerification; }
    }
}
