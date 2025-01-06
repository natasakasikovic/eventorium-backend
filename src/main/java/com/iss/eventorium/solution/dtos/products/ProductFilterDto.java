package com.iss.eventorium.solution.dtos.products;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterDto {
    private String name;
    private String description;
    private String type;
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private Boolean availability;
}