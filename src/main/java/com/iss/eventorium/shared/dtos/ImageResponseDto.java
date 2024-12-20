package com.iss.eventorium.shared.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {
    private byte[] data;
    private String contentType;
}
