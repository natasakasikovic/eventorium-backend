package com.iss.eventorium.solution.dtos.pricelists;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePriceRequestDto {
    @NotBlank(message = "Price is mandatory")
    private Double price;
    @NotBlank(message = "Discount is mandatory")
    private Double discount;
}
