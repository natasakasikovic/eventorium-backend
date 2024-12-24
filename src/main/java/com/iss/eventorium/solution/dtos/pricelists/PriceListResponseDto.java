package com.iss.eventorium.solution.dtos.pricelists;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceListResponseDto {
    private Long id;
    private String name;
    private Double price;
    private Double discount;
    private Double netPrice;
}
