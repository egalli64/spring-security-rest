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
public interface SecRoleRepository extends JpaRepository<SecRole, Long> {
    Optional<SecRole> findByName(String name);

    boolean existsByName(String name);
}
