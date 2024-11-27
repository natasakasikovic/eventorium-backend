package com.iss.eventorium.solution.util;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceFilter {
    private Long categoryId;
    private Long eventTypeId;
    private Boolean availability;
    private Double minPrice;
    private Double maxPrice;
}
