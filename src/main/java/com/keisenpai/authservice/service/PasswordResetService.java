package com.keisenpai.authservice.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keisenpai.authservice.domain.dto.PasswordResetRequestDto;
import com.keisenpai.authservice.domain.model.User;
import com.keisenpai.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.token-expiration-minutes}")
    private long tokenExpirationMinutes;

    @Transactional
    public void initiatePasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        userOptional.ifPresent(user -> {
            String resetToken = generateUniqueResetToken();
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetTokenExpiration(LocalDateTime.now().plusMinutes(tokenExpirationMinutes));
            
            userRepository.save(user);
            
            // Отправка email с токеном сброса
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        });
    }

    @Transactional
    public void resetPassword(PasswordResetRequestDto request) {
        User user = userRepository.findByPasswordResetToken(request.getResetToken())
                .filter(u -> u.getPasswordResetTokenExpiration().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        // Проверка совпадения паролей
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Обновление пароля
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiration(null);

        userRepository.save(user);
    }

    private String generateUniqueResetToken() {
        return UUID.randomUUID().toString();
    }
}