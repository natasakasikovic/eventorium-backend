package com.iss.eventorium.product.dtos;

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
    private Boolean available;
    private Boolean visible;
}
