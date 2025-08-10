# Spring Security REST Tutorial

## Branches

- 3.1 - Split from "classic" tutorial
- 3.2 - Introduction to JWT
- 3.3 - JWT in Practice

## 3.3 - JWT in Practice

- Feedback in case of JWT exceptions
	- JwtAuthenticationFilter: ExpiredJwtException and MalformedJwtException interrupt filter chain
- CORS support
	- SecurityConfig: using a CorsConfiguration object
- Support for testing
	- SecurityConfig: change permissions to some endpoints
	- AdminController: a minimal REST controller
	- index.html, login.js, commands.js: AJAX calls to the server
