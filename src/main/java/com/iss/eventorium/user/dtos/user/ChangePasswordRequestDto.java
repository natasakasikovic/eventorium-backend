package com.iss.eventorium.user.dtos.user;

import com.iss.eventorium.user.validators.password.PasswordsMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordsMatch
public class ChangePasswordRequestDto {
    @NotNull
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotNull
    @NotBlank(message = "Password is required")
    private String password;

    @NotNull
    @NotBlank(message = "Password confirmation is required")
    private String passwordConfirmation;
}
