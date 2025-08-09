/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import java.util.List;

public record LoginResponse(String token, String username, List<String> authorities) {
}
