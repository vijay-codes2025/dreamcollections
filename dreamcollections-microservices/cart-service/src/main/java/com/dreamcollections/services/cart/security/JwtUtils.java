package com.dreamcollections.services.cart.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    private Key jwtSecretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecretString.getBytes();
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(jwtSecretKey)
                   .requireIssuer(jwtIssuer)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    public Claims getClaimsFromJwtToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(jwtSecretKey)
                   .requireIssuer(jwtIssuer)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    // Method to extract specific claim like userId (assuming it's stored as "userId")
    public Long getUserIdFromJwtToken(String token) {
        Claims claims = getClaimsFromJwtToken(token); // Assumes token is already validated before this call if used standalone
        return claims.get("userId", Long.class); // Make sure "userId" is the claim name used by IdentityService
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .requireIssuer(jwtIssuer) // Validate issuer
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
        } catch (SignatureException e) {
            logger.error("JWT signature validation failed: {}", e.getMessage());
        } catch (MissingClaimException e) {
            logger.error("JWT token is missing issuer claim or issuer is incorrect: {}", e.getMessage());
        }
        return false;
    }
}
