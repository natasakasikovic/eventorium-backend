package com.iss.eventorium.event.mappers;

import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public BudgetMapper(ModelMapper modelMapper) {
        BudgetMapper.modelMapper = modelMapper;
    }


    public static BudgetItem fromRequest(BudgetItemRequestDto dto) {
        return modelMapper.map(dto, BudgetItem.class);
    }

    public static BudgetItemResponseDto toResponse(BudgetItem item) {
        BudgetItemResponseDto dto = modelMapper.map(item, BudgetItemResponseDto.class);
        dto.setCategory(CategoryMapper.toResponse(item.getCategory()));
        //TODO: history!
        dto.setSpentAmount(item.getSolution().getPrice());
        return dto;
    }

    public static BudgetResponseDto toResponse(Budget budget) {
        return BudgetResponseDto.builder()
                .spentAmount(budget.getSpentAmount())
                .plannedAmount(budget.getPlannedAmount())
                .items(budget.getItems().stream().map(BudgetMapper::toResponse).toList())
                .build();

    }
}
