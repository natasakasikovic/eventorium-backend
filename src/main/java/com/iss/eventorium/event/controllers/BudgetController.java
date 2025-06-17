package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.api.BudgetApi;
import com.iss.eventorium.event.dtos.budget.*;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.SolutionReviewResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class BudgetController implements BudgetApi {

    private final BudgetService budgetService;

    @GetMapping("/events/{event-id}/budget")
    public ResponseEntity<BudgetResponseDto> getBudget(@PathVariable("event-id") Long eventId) {
        return ResponseEntity.ok(budgetService.getBudget(eventId));
    }

    @GetMapping("/events/{event-id}/budget/budget-items")
    public ResponseEntity<List<BudgetItemResponseDto>> getBudgetItems(@PathVariable("event-id") Long eventId) {
        return ResponseEntity.ok(budgetService.getBudgetItems(eventId));
    }

    @GetMapping("/events/{event-id}/budget/suggestions")
    public ResponseEntity<List<BudgetSuggestionResponseDto>> getBudgetSuggestions(
            @PathVariable("event-id") Long eventId,
            @RequestParam("category-id") Long categoryId,
            @RequestParam("price") Double price
    ) {
        return ResponseEntity.ok(budgetService.getBudgetSuggestions(eventId, categoryId, price));
    }

    @GetMapping("/budget-items")
    public ResponseEntity<List<SolutionReviewResponseDto>> getAllBudgetItems() {
        return ResponseEntity.ok(budgetService.getAllBudgetItems());
    }

    @PostMapping("/events/{event-id}/budget/budget-items")
    public ResponseEntity<BudgetItemResponseDto> createBudgetItem(
            @PathVariable("event-id") Long eventId,
            @Valid @RequestBody BudgetItemRequestDto request
    ) {
        return new ResponseEntity<>(budgetService.createBudgetItem(eventId, request), HttpStatus.CREATED);
    }

    @PostMapping("/events/{event-id}/budget/purchase")
    public ResponseEntity<ProductResponseDto> purchaseProduct(
            @PathVariable("event-id") Long eventId,
            @Valid @RequestBody BudgetItemRequestDto budgetItemRequestDto
    ) {
        return new ResponseEntity<>(budgetService.purchaseProduct(eventId, budgetItemRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/events/{event-id}/budget/budget-items/{item-id}")
    public ResponseEntity<BudgetItemResponseDto> updateBudgetItemPlannedAmount(
            @PathVariable("event-id") Long eventId,
            @PathVariable("item-id") Long itemId,
            @Valid @RequestBody UpdateBudgetItemRequestDto request
    ) {
        return ResponseEntity.ok((budgetService.updateBudgetItem(eventId, itemId, request)));
    }

    @DeleteMapping("/events/{event-id}/budget/budget-items/{item-id}")
    public ResponseEntity<Void> deleteBudgetItem(
            @PathVariable("event-id") Long eventId,
            @PathVariable("item-id") Long itemId
    ) {
        budgetService.deleteBudgetItem(eventId, itemId);
        return ResponseEntity.noContent().build();
    }
}
