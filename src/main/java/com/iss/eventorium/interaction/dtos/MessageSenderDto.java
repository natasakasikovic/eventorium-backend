package com.iss.eventorium.interaction.dtos;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSenderDto {
    private Long id;
    private String name;
    private String lastname;
}
