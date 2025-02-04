package com.iss.eventorium.user.dtos.auth;

import com.iss.eventorium.shared.dtos.CityDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "City is required")
    private CityDto city;
}