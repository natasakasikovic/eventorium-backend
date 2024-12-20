package com.iss.eventorium.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class QuickRegistrationRequestDto {
    private String name;
    private String lastname;
    private String email;
    private String password;
}