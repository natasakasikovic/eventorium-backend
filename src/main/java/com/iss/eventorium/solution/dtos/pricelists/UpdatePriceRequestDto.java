package com.iss.eventorium.solution.dtos.pricelists;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePriceRequestDto {
    private Double price;
    private Double discount;
}
