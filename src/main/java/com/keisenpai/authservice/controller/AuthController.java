package com.keisenpai.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keisenpai.authservice.domain.dto.AuthRequestDto;
import com.keisenpai.authservice.domain.dto.AuthResponseDto;
import com.keisenpai.authservice.domain.dto.RegistrationRequestDto;
import com.keisenpai.authservice.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and Registration API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid registration data")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegistrationRequestDto registrationRequest
    ) {
        return ResponseEntity.ok(authService.register(registrationRequest));
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate user", description = "Logs in a user and returns JWT token")
    @ApiResponse(responseCode = "200", description = "User authenticated successfully")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthResponseDto> authenticate(
            @Valid @RequestBody AuthRequestDto authRequest
    ) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verifies user email using token")
    @ApiResponse(responseCode = "200", description = "Email verified successfully")
    @ApiResponse(responseCode = "400", description = "Invalid verification token")
    public ResponseEntity<String> verifyEmail(
            @RequestParam("token") String token
    ) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }
}