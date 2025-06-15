package com.iss.eventorium.event.dtos.budget;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBudgetItemRequestDto {

    @NotNull(message = "Planned amount is mandatory")
    private Double plannedAmount;

}
