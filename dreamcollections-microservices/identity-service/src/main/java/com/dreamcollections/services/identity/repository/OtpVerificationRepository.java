package com.dreamcollections.services.identity.repository;

import com.dreamcollections.services.identity.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    // Find active OTP for phone number
    Optional<OtpVerification> findByPhoneNumberAndVerifiedFalseAndExpiresAtAfter(
            String phoneNumber, LocalDateTime currentTime);

    // Find active OTP for email
    Optional<OtpVerification> findByEmailAndVerifiedFalseAndExpiresAtAfter(
            String email, LocalDateTime currentTime);

    // Find active OTP for phone or email
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "(o.phoneNumber = :phoneNumber OR o.email = :email) AND " +
           "o.verified = false AND o.expiresAt > :currentTime " +
           "ORDER BY o.createdAt DESC")
    Optional<OtpVerification> findActiveOtpByPhoneOrEmail(
            @Param("phoneNumber") String phoneNumber, 
            @Param("email") String email, 
            @Param("currentTime") LocalDateTime currentTime);

    // Count recent attempts for rate limiting
    @Query("SELECT COUNT(o) FROM OtpVerification o WHERE " +
           "o.phoneNumber = :phoneNumber AND " +
           "o.createdAt > :since")
    Long countRecentAttemptsByPhone(
            @Param("phoneNumber") String phoneNumber, 
            @Param("since") LocalDateTime since);

    // Clean up expired OTPs
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.expiresAt < :currentTime")
    void deleteExpiredOtps(@Param("currentTime") LocalDateTime currentTime);

    // Find all OTPs for a user (for cleanup)
    List<OtpVerification> findByUserId(Long userId);

    // Mark OTP as verified
    @Modifying
    @Query("UPDATE OtpVerification o SET o.verified = true WHERE o.id = :id")
    void markAsVerified(@Param("id") Long id);
}
