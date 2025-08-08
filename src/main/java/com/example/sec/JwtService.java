/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private static final Logger log = LogManager.getLogger(JwtService.class);

    /** !!! a proper secret key management system should be used !!! */
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    /** 1 day in milliseconds */
    private static final int EXPIRATION_TIME = 86400000; // 24 hours in milliseconds

    private final Key key;

    public JwtService() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    /**
     * Generate JWT token from Spring Security UserDetails
     */
    public String generateToken(UserDetails userDetails) {
        log.debug("Generating token for user: {}", userDetails.getUsername());

        List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder().subject(userDetails.getUsername()) // 'sub' is the username
                .claim("authorities", authorities) // Roles and permissions
                .issuedAt(new Date(System.currentTimeMillis())) //
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //
                .signWith(key) //
                .compact(); // build the token as string
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return claim(token, Claims::getSubject);
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        return expiration(token).before(new Date());
    }

    /**
     * Validate token against user details
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        return claim(token, Claims::getSubject).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Helpers

    private Date expiration(String token) {
        return claim(token, Claims::getExpiration);
    }

    private <T> T claim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(allClaims(token));
    }

    private Claims allClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }
}
