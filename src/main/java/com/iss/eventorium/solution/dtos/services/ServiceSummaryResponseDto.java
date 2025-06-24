package com.iss.eventorium.solution.dtos.services;

import com.iss.eventorium.shared.models.Status;
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
    private Double discount;
    private Double rating;
    private Boolean available;
    private Boolean visible;
    private Status status;
}
