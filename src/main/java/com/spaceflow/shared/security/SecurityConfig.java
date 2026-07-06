package com.spaceflow.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Stateless security: JWT resource server for the API, OAuth2 login (Google/GitHub)
 * for issuing tokens. Swagger and actuator health are public.
 *
 * <p>Active in all profiles except {@code dev}, which swaps in a permissive chain
 * ({@link DevSecurityConfig}) so the app boots without an external OIDC provider.
 */
@Configuration
@Profile("!dev")
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC = {
            "/swagger-ui/**", "/v3/api-docs/**",
            "/actuator/health/**", "/actuator/prometheus",
            "/oauth2/**", "/login/**"
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/resources").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2Login(login -> {})
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> {}));
        return http.build();
    }
}
