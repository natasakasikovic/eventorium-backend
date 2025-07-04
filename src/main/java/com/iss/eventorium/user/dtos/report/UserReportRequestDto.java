package com.iss.eventorium.user.dtos.report;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportRequestDto {
    @NotBlank(message = "Report must contain reason!")
    private String reason;
}