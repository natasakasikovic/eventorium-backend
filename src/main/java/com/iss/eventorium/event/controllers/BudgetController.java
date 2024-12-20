package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.BudgetRequestDto;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.solution.dtos.products.ProductRequestDto;
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

    @GetMapping("/purchased")
    public ResponseEntity<?> getPurchasedItems(@PathVariable("event-id") Long eventId) {
        return null;
    }

}
