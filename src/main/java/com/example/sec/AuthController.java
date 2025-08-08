/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * <pre>
  curl -X POST http://localhost:8080/api/login -H "Content-Type: application/json" ^
   -d "{\"username\":\"admin\",\"password\":\"admin\"}"
   
   curl -H "Authorization: Bearer eyJ0eXAiOiJKV1Q..." http://localhost:8080/admin
 * </pre>
 */
@RestController
@RequestMapping("/api")
public class AuthController {
    private static final Logger log = LogManager.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.debug("login({})", loginRequest.getUsername());

        try {
            // Step 1: Validate credentials using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            log.debug("Authentication successful for user: {}", loginRequest.getUsername());

            // Step 2: Extract the principal from the authentication
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Step 3: Generate JWT token
            String jwtToken = jwtService.generateToken(userDetails);

            // Step 4: Create response with token and user info
            LoginResponse response = new LoginResponse(jwtToken, userDetails.getUsername(),
                    userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

            log.info("JWT token generated for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password"));

        } catch (DisabledException e) {
            log.warn("Account disabled for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Account is disabled"));

        } catch (LockedException e) {
            log.warn("Account locked for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Account is locked"));

        } catch (AuthenticationException e) {
            log.error("Authentication error for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Authentication failed"));
        }
    }
}
