package com.iss.eventorium.user.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderResponseDto {
    private Long id;
    private String name;
    private String lastname;
}
