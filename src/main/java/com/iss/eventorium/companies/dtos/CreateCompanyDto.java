package com.iss.eventorium.companies.dtos;

import com.iss.eventorium.users.models.City;

import java.sql.Time;
import java.util.ArrayList;

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
}
