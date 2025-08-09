# Spring Security REST Tutorial

## Branches

- 3.1 - Split from "classic" tutorial
- 3.2 - Introduction to JWT

## 3.2 - Introduction to JWT

- Dependencies in pom.xml
	- jjwt: api, impl, jackson
	- spring boot validation: use of @Valid in controller
- JWT
	- JwtService (new): Generation and validation of tokens
	- JwtAuthenticationFilter (new): from JWT token to standard SecurityContext handling
	- SecurityConfig: switch to stateless management
- Login with JWT
	- ErrorResponse, LoginRequest, LoginResponse (new): DTO support
	- AuthController (new): support to /api/login
