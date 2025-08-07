/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Manage the SecUser to UserDetails relation
 */
@Service
public class SecUserDetailsService implements UserDetailsService {
    private static final Logger log = LogManager.getLogger(SecUserDetailsService.class);

    private final SecUserService svc;

    public SecUserDetailsService(SecUserService svc) {
        this.svc = svc;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.traceEntry("loadUserByUsername({})", username);

        SecUser user = svc.findByUsername(username).orElseThrow(() -> {
            log.warn("User '{}' not found", username);
            return new UsernameNotFoundException("User not found: " + username);
        });

        log.debug("Found user '{}' with roles {} and authorities {}", //
                user.getUsername(), user.getRoles(), user.getAuthorities());

        // Convert business user to Security UserDetails
        return User.builder().username(user.getUsername()).password(user.getPassword())
                .authorities(mapRolesToAuthorities(user)).accountExpired(user.isAccountExpired())
                .accountLocked(user.isAccountLocked()).credentialsExpired(user.isCredentialsExpired())
                .disabled(!user.isEnabled()).build();
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(SecUser user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Add roles with ROLE_ prefix
        user.getRoleNames().stream().map(role -> "ROLE_" + role).map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        // Add authorities as-is
        user.getAuthorityNames().stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);

        return authorities;
    }
}
