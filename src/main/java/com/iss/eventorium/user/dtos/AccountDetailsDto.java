package com.iss.eventorium.user.dtos;

import com.iss.eventorium.shared.models.City;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetailsDto {
    private Long id;
    private String email;
    private String name;
    private String lastname;
    private String phoneNumber;
    private String address;
    private City city;
    private String role;
}