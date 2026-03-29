package com.portal.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(auth -> auth
                .pathMatchers(
                    "/actuator/health",
                    "/fallback/**",
                    "/api/v1/*/health"
                ).permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> {})
            );
        return http.build();
    }
}

// package com.portal.gateway.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
// import org.springframework.security.config.web.server.ServerHttpSecurity;
// import org.springframework.security.web.server.SecurityWebFilterChain;

// @Configuration
// @EnableWebFluxSecurity
// public class SecurityConfig {

//     @Bean
//     public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//         http
//             .csrf(csrf -> csrf.disable())
//             .authorizeExchange(auth -> auth
//                 .anyExchange().permitAll()
//             );
//         return http.build();
//     }
// }



// Commented code is for direct testing access denied but access thorugh portal is allowed with IWT token exist in pportal, to check if this working correctly we used commendted code and then chechked in postman the output is fine.
/*Why 401 is actually GOOD news:

401 = Unauthorized — the gateway received the request and rejected it because there's no JWT token
This means the gateway is routing correctly and security is working
When you access via browser directly it has no auth token — that's expected */


// Create a new POST request in Postman:
// POST http://localhost:8080/realms/manufacturing-portal/protocol/openid-connect/token
// Under Body → select x-www-form-urlencoded → add:
// KeyValuegrant_typepasswordclient_idportal-frontendusernameadmin-userpasswordAdmin@123
// Click Send → you'll get a response with access_token.

// Step 3 — Use the token to call gateway:
// Create a new GET request:
// GET http://localhost:8888/api/v1/programs
// Under Authorization:

// Type: Bearer Token
// Token: paste the access_token from Step 2

// Click Send → you'll see programs JSON! ✅
