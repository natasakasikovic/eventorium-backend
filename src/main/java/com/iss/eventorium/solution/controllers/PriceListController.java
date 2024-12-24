package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.pricelists.PriceListResponseDto;
import com.iss.eventorium.solution.dtos.pricelists.PriceListUpdateRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/price-list")
public class PriceListController {

    @GetMapping("/services/all")
    public ResponseEntity<List<ServiceResponseDto>> getPriceListServices() {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/services")
    public ResponseEntity<PagedResponse<PriceListResponseDto>> getPriceListServices(Pageable pageable) {
        return ResponseEntity.ok(new PagedResponse<>(List.of(new PriceListResponseDto()), 1, 100));
    }

    @GetMapping("/products/all")
    public ResponseEntity<List<PriceListResponseDto>> getPriceListProducts() {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/products")
    public ResponseEntity<PagedResponse<PriceListResponseDto>> getPriceListProducts(Pageable pageable) {
        return ResponseEntity.ok(new PagedResponse<>(List.of(new PriceListResponseDto()), 1, 100));
    }

    @PatchMapping("/services/{id}")
    public ResponseEntity<PriceListResponseDto> updateServicePrice(
            @PathVariable Long id,
            @Valid @RequestBody PriceListUpdateRequestDto updateRequestDto
    ) {
        return ResponseEntity.ok(new PriceListResponseDto());
    }

    @PatchMapping("/products/{id}")
    public ResponseEntity<PriceListResponseDto> updateProductPrice(
            @PathVariable Long id,
            @RequestBody PriceListUpdateRequestDto updateRequestDto
    ) {
        return ResponseEntity.ok(new PriceListResponseDto());
    }
}
