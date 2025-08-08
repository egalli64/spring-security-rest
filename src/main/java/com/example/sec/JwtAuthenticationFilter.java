/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LogManager.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final SecUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, SecUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("Processing request: {}", request.getRequestURI());

        // Step 1: Extract JWT token from Authorization header
        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("No JWT token found in request to {}", request.getRequestURI());
            filterChain.doFilter(request, response); // no authentication
            return;
        }

        // Step 2: Get rid of the prefix
        final String jwt = header.substring(7);

        try {
            // Step 3: Extract username from token
            final String username = jwtService.extractUsername(jwt);
            log.debug("JWT token found for user: {}", username);

            // Step 4: If user is not already authenticated
            SecurityContext context = SecurityContextHolder.getContext();
            if (username != null && context.getAuthentication() == null) {

                // Step 5: Load user details to validate token
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Step 6: Validate token
                if (jwtService.validateToken(jwt, userDetails)) {
                    log.debug("JWT token validated successfully for user: {}", username);

                    // Step 7: Create a UsernamePasswordAuthenticationToken
                    // with the principal, (no credential, not required for JWT), and authorities
                    var token = new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());

                    // Step 8: Add request details
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Step 9: Set authentication in SecurityContext
                    context.setAuthentication(token);

                    log.info("User {} authenticated via JWT", username);
                } else {
                    log.warn("JWT token validation failed for user: {}", username);
                }
            }
        } catch (Exception e) {
            // Don't stop the filter chain - just log the error
            log.error("JWT authentication error: {}", e.getMessage());
        }

        // Step 10: Continue with the request (authenticated or not)
        filterChain.doFilter(request, response);
    }
}
