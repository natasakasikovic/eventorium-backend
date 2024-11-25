package com.iss.eventorium.companies.dtos;

import com.iss.eventorium.users.models.City;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyDto {
    private String name;
    private String address;
    private City city;
    private String phoneNumber;
    private String description;
    private String email;
    private ArrayList<String> photos;
    private Time openingHours;
    private Time closingHours;
    private Long providerId;
}
