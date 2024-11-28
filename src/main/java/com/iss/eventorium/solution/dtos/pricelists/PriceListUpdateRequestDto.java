package com.iss.eventorium.solution.dtos.pricelists;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceListUpdateRequestDto {
    private Double price;
    private Double discount;
}
