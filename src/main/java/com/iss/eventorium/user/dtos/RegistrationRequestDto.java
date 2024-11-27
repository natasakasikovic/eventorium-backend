package com.iss.eventorium.user.dtos;

import com.iss.eventorium.user.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {
    private String email;
    private String password;
    private String name;
    private String lastname;
    private String phoneNumber;
    private String address;
    private String city;
    private Role role;
}