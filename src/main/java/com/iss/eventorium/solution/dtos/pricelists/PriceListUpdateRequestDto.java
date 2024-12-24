package com.iss.eventorium.solution.dtos.pricelists;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceListUpdateRequestDto {
    @NotBlank(message = "Price is mandatory")
    private Double price;
    @NotBlank(message = "Discount is mandatory")
    private Double discount;
}
