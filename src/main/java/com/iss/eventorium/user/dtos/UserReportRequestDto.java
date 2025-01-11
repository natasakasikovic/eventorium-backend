package com.iss.eventorium.user.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportRequestDto {
    @NotBlank(message = "Report must contain reason!")
    private String reason;
}