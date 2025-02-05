package com.iss.eventorium.shared.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentableResponseDto {
    private Long id;
    private String displayName;
}
