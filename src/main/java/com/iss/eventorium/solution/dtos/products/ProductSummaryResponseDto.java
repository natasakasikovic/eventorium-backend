package com.iss.eventorium.solution.dtos.products;

import com.iss.eventorium.shared.models.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaryResponseDto {
    private Long id;
    private String name;
    private Double price;
    private Double discount;
    private Boolean available;
    private Boolean visible;
    private Double rating;
    private Status status;
}
