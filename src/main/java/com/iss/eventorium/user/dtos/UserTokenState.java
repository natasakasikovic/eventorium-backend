package com.iss.eventorium.user.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenState {
    private String jwt;
    private Long expiresIn;
}
