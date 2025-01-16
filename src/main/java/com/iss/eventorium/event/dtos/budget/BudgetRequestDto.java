package com.iss.eventorium.event.dtos.budget;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequestDto {
    private List<BudgetItemRequestDto> items;
}
