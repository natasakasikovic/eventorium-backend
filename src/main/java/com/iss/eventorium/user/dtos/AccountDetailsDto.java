package com.iss.eventorium.user.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetailsDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String city;
    private String role;
}