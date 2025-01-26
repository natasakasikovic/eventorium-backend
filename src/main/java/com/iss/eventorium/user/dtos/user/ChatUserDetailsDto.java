package com.iss.eventorium.user.dtos.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUserDetailsDto {
    private Long id;
    private String name;
    private String lastname;
}
