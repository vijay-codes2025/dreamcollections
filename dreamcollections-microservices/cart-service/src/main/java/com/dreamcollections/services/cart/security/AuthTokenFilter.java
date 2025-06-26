package com.dreamcollections.services.cart.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
// UserPrincipal will be used instead of Spring's User
// import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);
                Long userId = jwtUtils.getUserIdFromJwtToken(jwt); // Extract userId

                if (userId == null) {
                    logger.warn("User ID not found in JWT token for username: {}. Cannot authenticate.", username);
                    filterChain.doFilter(request, response); // Continue chain without auth
                    return;
                }

                Claims claims = jwtUtils.getClaimsFromJwtToken(jwt); // Already validated
                String rolesClaim = claims.get("roles", String.class);

                List<GrantedAuthority> authorities = Collections.emptyList();
                if (StringUtils.hasText(rolesClaim)) {
                    authorities = Arrays.stream(rolesClaim.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                } else {
                    logger.warn("No roles claim found in JWT for user {} in Cart Service", username);
                }

                UserPrincipal principal = new UserPrincipal(userId, username, authorities);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("User {} (ID: {}) authenticated in Cart Service with roles {} for path {}",
                             username, userId, authorities, request.getServletPath());
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication in Cart Service: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
