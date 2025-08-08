/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {
    private final SecUserDetailsService svc;

    public SecurityConfig(SecUserDetailsService secUserSvc) {
        this.svc = secUserSvc;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Wrap a SpEL to be used in the access() call
        final WebExpressionAuthorizationManager adminWithReportsAccess = new WebExpressionAuthorizationManager(
                "hasRole('ADMIN') and hasAuthority('VIEW_REPORTS')");

        // Disable HTTP Basic - using form login instead
        http.httpBasic(httpBasic -> httpBasic.disable())
                // Enable form login - custom login page
                .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/do_login")
                        // redirections after success/failure
                        .defaultSuccessUrl("/", true).failureUrl("/login?error=true") //
                        .permitAll())
                // logout configuration
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll())
                // use the app-specific UserDetailsService
                .userDetailsService(svc)
                // Two roles, an authority + public and H2 console
                .authorizeHttpRequests(auth -> auth.requestMatchers("/", "/public", "/h2-console/**").permitAll() //
                        .requestMatchers("/admin").hasRole("ADMIN") //
                        .requestMatchers("/private").hasRole("USER") //
                        .requestMatchers("/reports").hasAuthority("VIEW_REPORTS") //
                        .requestMatchers("/admin/reports").access(adminWithReportsAccess) //
                        // require only to be logged - see service for security check
                        .requestMatchers("/users/**").authenticated() //
                        .anyRequest().denyAll())
                // Disable Cross-Site Request Forgery for H2
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}
