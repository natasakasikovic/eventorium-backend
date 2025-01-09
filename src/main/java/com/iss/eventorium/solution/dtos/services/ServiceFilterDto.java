package com.iss.eventorium.solution.dtos.services;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceFilterDto {
    private String name;
    private String description;
    private String type;
    private String category;
    private Boolean availability;

    @Min(value = 0, message = "The value of minimal price must be greater than or equal to zero!")
    private Double minPrice;

    @Positive(message = "The value of maximum price must be greater than zero!")
    private Double maxPrice;
}
