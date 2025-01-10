package com.iss.eventorium.user.dtos;

import com.iss.eventorium.shared.dtos.CityDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDto {

    @NotBlank(message = "First name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastname;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+)?[0-9]{9,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "City is required")
    private CityDto city;

    private String profilePhoto;
}
