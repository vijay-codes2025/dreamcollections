package com.dreamcollections.services.order.config; // Or a more general 'client.interceptor' package

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken; // If using this token type
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;


@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        // Try to get Authorization header from current HttpServletRequest
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                template.header(AUTHORIZATION_HEADER, authorizationHeader);
                return; // Found and applied from HttpServletRequest
            }
        }

        // Fallback: Try to get token from SecurityContextHolder (if request context is not available, e.g. async)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) { // Check if principal is JwtAuthenticationToken
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            String tokenValue = jwtAuth.getToken().getTokenValue();
            if (tokenValue != null) {
                template.header(AUTHORIZATION_HEADER, "Bearer " + tokenValue);
            }
        }
        // Add more specific checks if authentication.getCredentials() or getPrincipal() holds the token directly
        // depending on how AuthTokenFilter sets up the Authentication object.
        // For now, JwtAuthenticationToken is a common case if using Spring Security's resource server capabilities.
    }
}
