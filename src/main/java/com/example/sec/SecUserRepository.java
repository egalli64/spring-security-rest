/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SecUserRepository extends JpaRepository<SecUser, Long> {
    
    Optional<SecUser> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u.username FROM SecUser u")
    Set<String> findAllUsernames();
}
