package com.iss.eventorium.shared.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {
    private Long id;
    private byte[] data;
    private String contentType;

    public ImageResponseDto(final byte[] data, final String contentType) {
        this.data = data;
        this.contentType = contentType;
    }
}
