package com.smartcourier.auth.config;

import com.smartcourier.auth.security.JwtAuthenticationFilter;
import com.smartcourier.auth.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

/**
 * {@link org.springframework.boot.test.mock.mockito.MockBean} on {@link JwtAuthenticationFilter} breaks
 * {@link org.springframework.test.web.servlet.MockMvc} on newer JDKs: {@code GenericFilterBean} expects a real
 * filter instance during {@code init()}. A no-op subclass keeps security chain wiring without parsing JWT in slice tests.
 */
@TestConfiguration
public class JwtAuthenticationFilterTestConfig {

    @Bean
    @Primary
    JwtAuthenticationFilter jwtAuthenticationFilter(UserDetailsService userDetailsService) {
        JwtService jwtService = Mockito.mock(JwtService.class);
        return new JwtAuthenticationFilter(jwtService, userDetailsService) {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                filterChain.doFilter(request, response);
            }
        };
    }
}
