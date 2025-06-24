package com.iss.eventorium.solution.dtos.pricelists;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePriceRequestDto {
    @NotNull(message = "Price is mandatory")
    @Min(value = 0, message = "Price must be non-negative")
    private Double price;

    @NotNull(message = "Discount is mandatory")
    @Min(value = 0, message = "Discount must be non-negative")
    @Max(value = 100, message = "Discount cannot exceed 100")
    private Double discount;
}
