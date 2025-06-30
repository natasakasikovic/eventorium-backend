package com.iss.eventorium.user.dtos.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportResponseDto {
    private Long id;
    private LocalDateTime timestamp;
    private String reason;
    private String reporter;
    private String offender;
}