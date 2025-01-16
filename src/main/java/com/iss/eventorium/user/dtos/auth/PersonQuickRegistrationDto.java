package com.iss.eventorium.user.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PersonQuickRegistrationDto {

    @NotBlank(message = "First name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastname;
}