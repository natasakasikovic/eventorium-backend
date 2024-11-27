package com.iss.eventorium.event.controllers;

import com.iss.eventorium.solution.dtos.products.ProductRequestDto;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/events/{event-id}/budget")
public class BudgetController {

    @PutMapping("/items/{product-id}")
    public ResponseEntity<ProductResponseDto> purchaseProduct(
            @PathVariable("event-id") Long eventId,
            @PathVariable("product-id") Long productId,
            @RequestBody ProductRequestDto productRequestDto
    ) {
        return ResponseEntity.ok().body(new ProductResponseDto());
    }
}
