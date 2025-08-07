/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.security.RolesAllowed;

@Service
@Transactional
public class SecUserService {
    private final SecUserRepository userRepo;
    private final SecRoleRepository roleRepo;
    private final SecAuthorityRepository authRepo;
    private final PasswordEncoder encoder;

    public SecUserService(SecUserRepository userRepo, SecRoleRepository roleRepo, SecAuthorityRepository authRepo,
            PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.authRepo = authRepo;
        this.encoder = encoder;
    }

    @RolesAllowed("ADMIN")
    public SecUser create(String username, String password, Set<String> roleNames, Set<String> authNames) {
        if (userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("User already exists: " + username);
        }

        Set<SecRole> roles = new HashSet<>();
        for (String name : roleNames) {
            SecRole role = roleRepo.findByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name));
            roles.add(role);
        }

        Set<SecAuthority> auths = new HashSet<>();
        for (String name : authNames) {
            SecAuthority auth = authRepo.findByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("Authorization not found: " + name));
            auths.add(auth);
        }

        SecUser user = new SecUser(username, encoder.encode(password), roles);
        return userRepo.save(user);
    }

    // It's always possible to get a user (otherwise, how to login?)
    @Transactional(readOnly = true)
    public Optional<SecUser> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @RolesAllowed("ADMIN")
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    @PreAuthorize("hasRole('ADMIN') or authentication.name == #username")
    public boolean updatePassword(String username, String password) {
        Optional<SecUser> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            SecUser user = userOpt.get();
            user.setPassword(encoder.encode(password));
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @RolesAllowed("ADMIN")
    public boolean updateRoles(String username, Set<String> newRoleNames) {
        Optional<SecUser> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            SecUser user = userOpt.get();

            Set<SecRole> newRoles = new HashSet<>();
            for (String roleName : newRoleNames) {
                SecRole role = roleRepo.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
                newRoles.add(role);
            }

            user.setRoles(newRoles);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @RolesAllowed("ADMIN")
    public boolean setEnabled(String username, boolean enabled) {
        Optional<SecUser> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            SecUser user = userOpt.get();
            user.setEnabled(enabled);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @RolesAllowed("ADMIN")
    public boolean delete(String username) {
        Optional<SecUser> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            userRepo.delete(userOpt.get());
            return true;
        }
        return false;
    }

    @RolesAllowed({ "ADMIN", "USER" })
    @Transactional(readOnly = true)
    public Set<String> getAllUsernames() {
        return userRepo.findAllUsernames();
    }

    @RolesAllowed({ "ADMIN", "USER" })
    @Transactional(readOnly = true)
    public long getSize() {
        return userRepo.count();
    }

    /**
     * Enforce admin access
     * 
     * @throws AccessDeniedException if the user has not admin role
     */
    @RolesAllowed("ADMIN")
    public void requireAdminAccess() {
    }
}
