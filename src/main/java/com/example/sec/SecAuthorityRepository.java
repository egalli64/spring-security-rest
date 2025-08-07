/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecAuthorityRepository extends JpaRepository<SecAuthority, Long> {
    Optional<SecAuthority> findByName(String name);

    boolean existsByName(String name);
}
