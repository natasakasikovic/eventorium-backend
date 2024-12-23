package com.iss.eventorium.user.dtos;

import com.iss.eventorium.shared.validators.password.PasswordsMatch;
import com.iss.eventorium.user.models.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordsMatch(message = "Passwords do not match")
public class AuthRequestDto {

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotEmpty(message = "Role is required")
    private Collection<Role> role;

    @Valid
    private PersonRequestDto person;
}