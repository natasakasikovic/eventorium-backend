package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.SolutionReviewResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("api/v1")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/events/{event-id}/budget")
    public ResponseEntity<BudgetResponseDto> getBudget(@PathVariable("event-id") Long eventId) {
        return ResponseEntity.ok(budgetService.getBudget(eventId));
    }

    @PostMapping("/events/{event-id}/budget/purchase")
    public ResponseEntity<ProductResponseDto> purchaseProduct(
            @PathVariable("event-id") Long eventId,
            @RequestBody BudgetItemRequestDto budgetItemRequestDto
    ) {
        return new ResponseEntity<>(budgetService.purchaseProduct(eventId, budgetItemRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/events/{event-id}/budget/budget-items")
    public ResponseEntity<List<BudgetItemResponseDto>> getBudgetItems(@PathVariable("event-id") Long eventId) {
        return ResponseEntity.ok(budgetService.getBudgetItems(eventId));
    }

    @GetMapping("/budget-items")
    public ResponseEntity<List<SolutionReviewResponseDto>> getAllBudgetItems() {
        return ResponseEntity.ok(budgetService.getAllBudgetItems());
    }

}
