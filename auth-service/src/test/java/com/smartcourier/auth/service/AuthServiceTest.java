package com.smartcourier.auth.service;

import com.smartcourier.auth.dto.SignupRequest;
import com.smartcourier.auth.entity.Role;
import com.smartcourier.auth.entity.UserAccount;
import com.smartcourier.auth.repository.UserAccountRepository;
import com.smartcourier.auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AuthService authService;

    @Test
    void signupShouldCreateCustomerByDefault() {
        SignupRequest request = new SignupRequest("Asha Patel", "asha@example.com", "password123", "9999999999", null);
        UserAccount saved = new UserAccount();
        saved.setId(10L);
        saved.setFullName(request.fullName());
        saved.setEmail(request.email());
        saved.setRoles(Set.of(Role.ROLE_CUSTOMER));
        saved.setCreatedAt(Instant.now());

        when(userAccountRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(saved);
        when(jwtService.generateToken(saved.getEmail(), saved.getId(), Set.of("ROLE_CUSTOMER"))).thenReturn("token");
        when(jwtService.extractExpiration("token")).thenReturn(Instant.now().plusSeconds(3600));

        var response = authService.signup(request);

        assertThat(response.email()).isEqualTo("asha@example.com");
        assertThat(response.roles()).containsExactly("ROLE_CUSTOMER");
        assertThat(response.token()).isEqualTo("token");
    }
}
