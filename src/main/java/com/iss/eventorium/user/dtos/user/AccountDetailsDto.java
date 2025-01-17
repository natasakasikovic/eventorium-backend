package com.iss.eventorium.user.dtos.user;

import com.iss.eventorium.shared.dtos.CityDto;
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
    private CityDto city;
    private String role;
}