package com.keisenpai.authservice.domain.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Login is required")
    private String login;

    @NotBlank(message = "Firstname is required")
    private String firstname;

    @Nullable
    private String lastname;

    @NotBlank(message = "Patronname is required")
    private String patronname;

    private boolean emailVerified;
}
