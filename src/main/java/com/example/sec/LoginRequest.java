/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest( //
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") String password) {
}
