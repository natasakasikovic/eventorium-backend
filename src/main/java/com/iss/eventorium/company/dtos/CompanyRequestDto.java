package com.iss.eventorium.company.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.user.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDto {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Address is required.")
    private String address;

    @NotBlank(message = "City is required.")
    private City city;

    @NotBlank(message = "Phone number is required.")
    private String phoneNumber;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "Email is required.")
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime openingHours;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime closingHours;

    @NotBlank
    private Long providerId;
}
