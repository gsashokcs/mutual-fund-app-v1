package com.mutualfund.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Spring Security with Basic Authentication. Implements role-based
 * access control for admin and user endpoints.
 *
 * @author Mutual Fund Management System
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain with role-based access control.
     *
     * <p>Access rules: - Public: /api/v1/users/register, /actuator/**, /h2-console/**,
     * /swagger-ui/**, /api-docs/** - ADMIN only: /api/v1/admin/** - Authenticated users:
     * /api/v1/users/** (with ownership validation in service layer)
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // Public endpoints
                                        .requestMatchers("/api/v1/users/register")
                                        .permitAll()
                                        .requestMatchers("/actuator/**")
                                        .permitAll()
                                        .requestMatchers("/h2-console/**")
                                        .permitAll()
                                        .requestMatchers(
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**",
                                                "/swagger-ui.html")
                                        .permitAll()

                                        // Admin-only endpoints
                                        .requestMatchers("/api/v1/admin/**")
                                        .hasRole("ADMIN")

                                        // User endpoints - require authentication (ownership
                                        // validation in service layer)
                                        .requestMatchers("/api/v1/users/**")
                                        .authenticated()

                                        // All other requests require authentication
                                        .anyRequest()
                                        .authenticated())
                .httpBasic(basic -> {})
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    /**
     * Provides the password encoder for encrypting user passwords.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides the AuthenticationManager for authentication processing.
     *
     * @param authenticationConfiguration the authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
