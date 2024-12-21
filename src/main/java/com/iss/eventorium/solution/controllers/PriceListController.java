package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.pricelists.PriceListResponseDto;
import com.iss.eventorium.solution.dtos.pricelists.PriceListUpdateRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.services.PriceListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/price-list")
@RequiredArgsConstructor
public class PriceListController {

    private final PriceListService priceListService;

    @GetMapping("/services/all")
    public ResponseEntity<List<PriceListResponseDto>> getPriceListServices() {
        return ResponseEntity.ok(priceListService.getPriceListServices());
    }

    @GetMapping("/services")
    public ResponseEntity<PagedResponse<PriceListResponseDto>> getPriceListServices(Pageable pageable) {
        return ResponseEntity.ok(priceListService.getPriceListServicesPaged(pageable));
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
            @RequestBody PriceListUpdateRequestDto updateRequestDto
    ) {
        return ResponseEntity.ok(priceListService.updateService(id, updateRequestDto));
    }

    @PatchMapping("/products/{id}")
    public ResponseEntity<PriceListResponseDto> updateProductPrice(
            @PathVariable Long id,
            @RequestBody PriceListUpdateRequestDto updateRequestDto
    ) {
        return ResponseEntity.ok(new PriceListResponseDto());
    }
}
