package com.iss.eventorium.solution.dtos.services;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceFilterDto {
    private String category;
    private String eventType;
    private Boolean availability;
    private Double minPrice;
    private Double maxPrice;
}
