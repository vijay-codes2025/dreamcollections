package com.dreamcollections.services.product.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
// Removed Authentication and UserDetails as this service primarily validates
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
// import java.util.Date; // Not needed if not generating tokens
// import java.util.stream.Collectors; // Not needed if not generating tokens

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecretString;

    // @Value("${jwt.expiration.ms}") // Not strictly needed for validation
    // private int jwtExpirationMs;

    @Value("${jwt.issuer}")
    private String jwtIssuer; // Important for validation if issuer is checked

    private Key jwtSecretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecretString.getBytes();
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Token generation methods are typically not needed in resource services
    // unless they issue tokens for other purposes (e.g. service-to-service).
    // For now, Product Catalog Service only validates user JWTs.

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecretKey).build()
                   .parseClaimsJws(token).getBody().getSubject();
    }

    public Claims getClaimsFromJwtToken(String token) {
         // Make sure to set the correct issuer if it's being validated
        return Jwts.parserBuilder()
                   .setSigningKey(jwtSecretKey)
                   .requireIssuer(jwtIssuer) // Validate issuer
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            // Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
            Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .requireIssuer(jwtIssuer) // Add issuer validation
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (SignatureException e) { // Added for completeness
            logger.error("JWT signature validation failed: {}", e.getMessage());
        } catch (MissingClaimException e) { // Added for issuer validation
            logger.error("JWT token is missing issuer claim or issuer is incorrect: {}", e.getMessage());
        }

        return false;
    }
}
