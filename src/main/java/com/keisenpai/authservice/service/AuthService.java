package com.keisenpai.authservice.service;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keisenpai.authservice.domain.dto.AuthRequestDto;
import com.keisenpai.authservice.domain.dto.AuthResponseDto;
import com.keisenpai.authservice.domain.dto.RegistrationRequestDto;
import com.keisenpai.authservice.domain.model.Role;
import com.keisenpai.authservice.domain.model.User;
import com.keisenpai.authservice.domain.model.UserStatus;
import com.keisenpai.authservice.exception.EmailAlreadyExistsException;
import com.keisenpai.authservice.repository.UserRepository;
import com.keisenpai.authservice.util.ValidationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public AuthResponseDto register(RegistrationRequestDto request) {
        // Validate input
        ValidationUtils.validateRegistrationRequest(request);

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(Role.ROLE_USER))
                .status(UserStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .verificationToken(verificationToken)
                .build();

        userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);

        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponseDto authenticate(AuthRequestDto request) {
        // Validate input
        ValidationUtils.validateAuthRequest(request);

        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);

        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }
}