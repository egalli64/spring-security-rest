/*
 * Spring Boot Security REST tutorial 
 * 
 * https://github.com/egalli64/spring-security-rest
 */
package com.example.sec;

import java.util.List;

public class LoginResponse {
    private String token;
    private String username;
    private List<String> authorities;

    public LoginResponse(String token, String username, List<String> authorities) {
        this.token = token;
        this.username = username;
        this.authorities = authorities;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public List<String> authorities() {
        return authorities;
    }
}
