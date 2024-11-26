package com.iss.eventorium.shared.utils;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilter {
    private Long categoryId;
    private Long eventTypeId;
    private Boolean availability;
    private String description;
    private Double priceFrom;
    private Double priceTo;
}
