package com.dreamcollections.services.identity.security; // Changed package to .security

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
// UserDetails is still the type from Authentication.getPrincipal()
// We will cast it to UserPrincipal if it's our custom type.
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    @Value("${jwt.refresh.expiration.ms:2592000000}") // 30 days default
    private long jwtRefreshExpirationMs;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    private Key jwtSecretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecretString.getBytes();
        // Ensure key is strong enough for HS256 (32 bytes).
        // If shorter, this might not be ideal for production.
        // Keys.hmacShaKeyFor will generate a key of appropriate size if the input material is too short,
        // but it's better to provide a strong secret.
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username;
        Long userId;

        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            username = userPrincipal.getUsername();
            userId = userPrincipal.getId();
        } else if (principal instanceof UserDetails) {
            // Fallback if it's a standard UserDetails (though our UserDetailsServiceImpl now returns UserPrincipal)
            username = ((UserDetails) principal).getUsername();
            // In this fallback, userId might not be available directly. This indicates a potential setup issue.
            // For robustness, one might fetch user from DB here, but it's better if UserPrincipal is always used.
            logger.warn("Authentication principal is UserDetails, not UserPrincipal. UserId claim might be missing.");
            userId = null; // Or throw an error, or fetch from DB if critical
        } else {
            username = principal.toString(); // Generic fallback
            userId = null;
            logger.warn("Authentication principal is not an instance of UserDetails. UserId claim will be missing.");
        }

        String authorities = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(","));

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities) // Storing roles in the token
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date());

        if (userId != null) {
            jwtBuilder.claim("userId", userId); // Add userId claim
        }

        return jwtBuilder
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenFromUsernameAndRole(String username, String role) {
         return Jwts.builder()
                .setSubject(username)
                .claim("roles", role) // Assuming single role string for simplicity here
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecretKey).build()
                   .parseClaimsJws(token).getBody().getSubject();
    }

    public Claims getClaimsFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecretKey).build()
                .parseClaimsJws(token).getBody();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } // SignatureException might occur if key is incorrect or token tampered.
          // Keys.hmacShaKeyFor should provide a compatible key.

        return false;
    }

    /**
     * Generate refresh token with longer expiration
     */
    public String generateRefreshToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username;
        Long userId = null;

        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            username = userPrincipal.getUsername();
            userId = userPrincipal.getId();
        } else if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("type", "refresh") // Mark as refresh token
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date());

        if (userId != null) {
            jwtBuilder.claim("userId", userId);
        }

        return jwtBuilder
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Check if token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromJwtToken(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }
}
