package com.iss.eventorium.event.api;

import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetSuggestionResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.SolutionReviewResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface BudgetApi {
    ResponseEntity<BudgetResponseDto> getBudget(Long eventId);

    ResponseEntity<List<BudgetItemResponseDto>> getBudgetItems(Long eventId);

    ResponseEntity<List<BudgetSuggestionResponseDto>> getBudgetSuggestions(
            Long eventId,
            Long categoryId,
            Double price
    );

    ResponseEntity<List<SolutionReviewResponseDto>> getAllBudgetItems();

    ResponseEntity<BudgetItemResponseDto> createBudgetItem(
            Long eventId,
            BudgetItemRequestDto request
    );

    ResponseEntity<ProductResponseDto> purchaseProduct(
            Long eventId,
            BudgetItemRequestDto budgetItemRequestDto
    );

    ResponseEntity<BudgetItemResponseDto> updateBudgetItemPlannedAmount(
            Long eventId,
            Long itemId,
            BudgetItemRequestDto request
    );
}
