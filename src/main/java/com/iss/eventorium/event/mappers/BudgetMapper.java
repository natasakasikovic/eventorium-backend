package com.iss.eventorium.event.mappers;

import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetSuggestionResponseDto;
import com.iss.eventorium.event.models.Budget;
import com.iss.eventorium.event.models.BudgetItem;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.models.SolutionType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetMapper {

    private final ModelMapper modelMapper;
    private final CategoryMapper categoryMapper;

    public BudgetItem fromRequest(BudgetItemRequestDto dto, Solution solution) {
        BudgetItem item = modelMapper.map(dto, BudgetItem.class);
        item.setId(null);
        item.setCategory(solution.getCategory());
        item.setSolution(solution);
        return item;
    }

    public BudgetItemResponseDto toResponse(BudgetItem item) {
        BudgetItemResponseDto dto = modelMapper.map(item, BudgetItemResponseDto.class);
        Solution solution = item.getSolution();
        if(solution == null) {
            dto.setSolutionName("[DELETED]");
        } else {
            dto.setSolutionId(item.getSolution().getId());
            dto.setSolutionName(item.getSolution().getName());
        }

        dto.setCategory(categoryMapper.toResponse(item.getCategory()));
        dto.setPlannedAmount(item.getPlannedAmount());
        dto.setType(item.getItemType());
        if(item.getProcessedAt() != null) {
            dto.setSpentAmount(item.getSolution().getPrice() * (1 - item.getSolution().getDiscount() / 100));
        } else {
            dto.setSpentAmount(0.0);
        }
        return dto;
    }

    public BudgetResponseDto toResponse(Budget budget) {
        return BudgetResponseDto.builder()
                .plannedAmount(budget.getPlannedAmount())
                .spentAmount(budget.getSpentAmount())
                .activeCategories(budget.getActiveCategories().stream().map(categoryMapper::toResponse).toList())
                .build();

    }

    public BudgetSuggestionResponseDto toSuggestionResponse(Solution solution) {
        BudgetSuggestionResponseDto dto = new BudgetSuggestionResponseDto();
        dto.setDiscount(solution.getDiscount());
        dto.setPrice(solution.getPrice());
        dto.setId(solution.getId());
        dto.setName(solution.getName());
        if(solution instanceof Service) {
            dto.setSolutionType(SolutionType.SERVICE);
        } else {
            dto.setSolutionType(SolutionType.PRODUCT);
        }
        dto.setRating(solution.calculateAverageRating());
        return dto;
    }
}
