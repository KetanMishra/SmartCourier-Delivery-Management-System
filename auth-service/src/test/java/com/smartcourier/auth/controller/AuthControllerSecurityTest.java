package com.smartcourier.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourier.auth.config.JwtAuthenticationFilterTestConfig;
import com.smartcourier.auth.config.SecurityConfig;
import com.smartcourier.auth.dto.AuthResponse;
import com.smartcourier.auth.dto.UserSummaryResponse;
import com.smartcourier.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilterTestConfig.class})
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void wireUserDetailsService() {
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(
                User.withUsername("admin@example.com")
                        .password("password")
                        .authorities("ROLE_ADMIN")
                        .build()
        );
    }

    @Test
    void signupShouldBeAccessibleWithoutAuthentication() throws Exception {
        var request = new SignupPayload(
                "Kentan Customer",
                "kentancustomer@gmail.com",
                "Password@123",
                "1234567890",
                Set.of("CUSTOMER")
        );
        var response = new AuthResponse(
                1L,
                request.fullName(),
                request.email(),
                Set.of("ROLE_CUSTOMER"),
                "jwt-token",
                Instant.now().plusSeconds(3600)
        );

        when(authService.signup(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("kentancustomer@gmail.com"));
    }

    @Test
    void getUsersShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/auth/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getUsersShouldRejectNonAdminUser() throws Exception {
        mockMvc.perform(get("/api/auth/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersShouldAllowAdminUser() throws Exception {
        when(authService.getUsers()).thenReturn(List.of(
                new UserSummaryResponse(
                        1L,
                        "Admin User",
                        "admin@example.com",
                        "9999999999",
                        Set.of("ROLE_ADMIN"),
                        Instant.now()
                )
        ));

        mockMvc.perform(get("/api/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@example.com"));
    }

    private record SignupPayload(
            String fullName,
            String email,
            String password,
            String phoneNumber,
            Set<String> roles
    ) {
    }
}
