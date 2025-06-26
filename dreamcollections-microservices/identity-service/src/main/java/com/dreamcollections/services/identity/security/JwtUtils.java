package com.dreamcollections.services.identity.security; // Changed package to .security

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        String authorities = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", authorities) // Storing roles in the token
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
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
}
