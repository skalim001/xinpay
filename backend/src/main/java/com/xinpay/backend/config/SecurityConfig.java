package com.xinpay.backend.config;

import com.xinpay.backend.security.JwtAuthFilter;
import com.xinpay.backend.security.SignatureValidationFilter;
import com.xinpay.backend.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           SignatureValidationFilter signatureFilter) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtil);

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**", "/ping", "/error", "/", "/test/**",
                    "/uploads/**", "/api/upload", "/api/deposit/status/**",
                    "/api/inr-deposits/**", "/api/usdt-deposits/**",
                    "/api/inr-withdraw/**", "/api/accounts/**",
                    "/api/usdt-withdraw/**", "/api/bank-details/**",
                    "/api/notifications/**", "/api/user/**", "/api/commissions/**",
                    "/api/wallet/**", "/api/balance/**", "/api/test/**","/api/admin/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // Add signature filter first, then JWT
            .addFilterBefore(signatureFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(jwtAuthFilter, SignatureValidationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
