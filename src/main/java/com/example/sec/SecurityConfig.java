/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {
    private final OAuth2AuthenticationSuccessHandler handler;
    private final JwtAuthenticationFilter filter;

    public SecurityConfig(OAuth2AuthenticationSuccessHandler handler, JwtAuthenticationFilter filter) {
        this.handler = handler;
        this.filter = filter;
    }

    /**
     * Used to validate credentials during login
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable()) //
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/public/**").permitAll() //
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2.successHandler(handler))
                // keep the existing JWT configuration
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build();
    }
}
