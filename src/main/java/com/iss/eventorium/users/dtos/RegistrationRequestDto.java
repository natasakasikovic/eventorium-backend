package com.iss.eventorium.users.dtos;

import com.iss.eventorium.users.models.Role;
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