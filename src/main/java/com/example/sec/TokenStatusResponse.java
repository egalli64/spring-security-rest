/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import java.util.Date;

public record TokenStatusResponse(Date expiration, long timeUntilExpiration, boolean valid) {
}
