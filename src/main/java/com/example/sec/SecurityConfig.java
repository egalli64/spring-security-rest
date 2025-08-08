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
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {
    private final SecUserDetailsService svc;
    private final JwtAuthenticationFilter filter;

    public SecurityConfig(SecUserDetailsService svc, JwtAuthenticationFilter filter) {
        this.svc = svc;
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
        // Same SpEL expression as before
        final WebExpressionAuthorizationManager adminWithReportsAccess = new WebExpressionAuthorizationManager(
                "hasRole('ADMIN') and hasAuthority('VIEW_REPORTS')");

        // session-based featured disabled
        return http.formLogin(form -> form.disable()) // no login form
                .httpBasic(httpBasic -> httpBasic.disable()) // no HTTP Basic auth
                .logout(logout -> logout.disable()) // no traditional logout
                .csrf(csrf -> csrf.disable()) // no CSRF (stateless API)

                // different session management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // no changes
                .userDetailsService(svc)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/login", "/h2-console/**").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN") //
                        .requestMatchers("/private").hasRole("USER") //
                        .requestMatchers("/reports").hasAuthority("VIEW_REPORTS") //
                        .requestMatchers("/admin/reports").access(adminWithReportsAccess) //
                        .requestMatchers("/users/**").authenticated() //
                        .anyRequest().denyAll()) //

                // JWT filter
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                // still required by H2 console
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) //
                .build();
    }
}
