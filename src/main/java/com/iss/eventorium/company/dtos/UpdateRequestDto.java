package com.iss.eventorium.company.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iss.eventorium.shared.dtos.CityDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestDto {

    private Long id;

    @NotNull(message = "Address is required.")
    @NotEmpty(message = "Address is required")
    private String address;

    @NotNull(message = "City is required.")
    private CityDto city;

    @NotNull(message = "Phone number is required.")
    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Description is required.")
    @NotEmpty(message = "Description is required")
    @Size(max = 1024, message = "Description too long.")
    private String description;

    @NotNull(message = "Opening hours field is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime openingHours;

    @NotNull(message = "Closing hours field is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private LocalTime closingHours;
}
