package com.iss.eventorium.user.dtos.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsDto {
    private Long id;
    private String name;
    private String lastname;
}
