package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("api/v1/events/{event-id}/budget")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<ProductResponseDto> getBudget(@PathVariable("event-id") Long eventId) {
        return null;
    }

    @PostMapping
    public ResponseEntity<?> createBudget(@PathVariable("event-id") Long eventId){
        return null;
    }

    @PostMapping("/purchase")
    public ResponseEntity<ProductResponseDto> purchaseProduct(
            @PathVariable("event-id") Long eventId,
            @RequestBody BudgetItemRequestDto budgetItemRequestDto
    ) {
        return ResponseEntity.ok(budgetService.purchaseProduct(eventId, budgetItemRequestDto));
    }

    @GetMapping("/purchased")
    public ResponseEntity<?> getPurchasedItems(@PathVariable("event-id") Long eventId) {
        return null;
    }

}
