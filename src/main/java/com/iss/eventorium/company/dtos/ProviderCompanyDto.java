package com.iss.eventorium.company.dtos;

import com.iss.eventorium.shared.dtos.CityDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCompanyDto {
    private Long id;
    private String name;
    private String description;
    private String phoneNumber;
    private String email;
    private String address;
    private CityDto city;
    private LocalTime openingHours;
    private LocalTime closingHours;
}
