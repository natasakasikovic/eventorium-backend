package com.iss.eventorium.solution.dtos.services;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceSummaryResponseDto {
    private Long id;
    private String name;
    private Double price;
    private Boolean available;
    private Boolean visible;
}
