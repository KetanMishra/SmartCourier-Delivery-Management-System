package com.smartcourier.auth.service;

import com.smartcourier.auth.dto.AuthResponse;
import com.smartcourier.auth.dto.LoginRequest;
import com.smartcourier.auth.dto.SignupRequest;
import com.smartcourier.auth.dto.UserSummaryResponse;
import com.smartcourier.auth.entity.Role;
import com.smartcourier.auth.entity.UserAccount;
import com.smartcourier.auth.repository.UserAccountRepository;
import com.smartcourier.auth.security.JwtService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RabbitTemplate rabbitTemplate;

    public AuthService(UserAccountRepository userAccountRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       RabbitTemplate rabbitTemplate) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userAccountRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UserAccount user = new UserAccount();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(resolveRoles(request.roles()));
        UserAccount saved = userAccountRepository.save(user);

        rabbitTemplate.convertAndSend("smartcourier.user.audit", "USER_REGISTERED:" + saved.getEmail());
        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserAccount user = userAccountRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return buildAuthResponse(user);
    }

    public List<UserSummaryResponse> getUsers() {
        return userAccountRepository.findAll().stream().map(this::toSummary).toList();
    }

    public UserSummaryResponse getUser(Long id) {
        return userAccountRepository.findById(id).map(this::toSummary)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private AuthResponse buildAuthResponse(UserAccount user) {
        Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        String token = jwtService.generateToken(user.getEmail(), user.getId(), roles);
        Instant expiresAt = jwtService.extractExpiration(token);
        return new AuthResponse(user.getId(), user.getFullName(), user.getEmail(), roles, token, expiresAt);
    }

    private UserSummaryResponse toSummary(UserAccount user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                user.getCreatedAt()
        );
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return Set.of(Role.ROLE_CUSTOMER);
        }
        return requestedRoles.stream().map(String::toUpperCase).map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
