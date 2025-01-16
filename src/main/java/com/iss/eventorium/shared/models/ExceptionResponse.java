package com.iss.eventorium.shared.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionResponse {
    private String error;
    private String message;
}
