package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.shared.utils.ResponseHeaderUtils;
import com.iss.eventorium.solution.api.PriceListApi;
import com.iss.eventorium.solution.dtos.pricelists.PriceListResponseDto;
import jakarta.validation.Valid;
import com.iss.eventorium.solution.dtos.pricelists.UpdatePriceRequestDto;
import com.iss.eventorium.solution.services.PriceListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/price-list")
@RequiredArgsConstructor
public class PriceListController implements PriceListApi {

    private final PriceListService priceListService;

    @GetMapping("/services/all")
    public ResponseEntity<List<PriceListResponseDto>> getPriceListServices() {
        return ResponseEntity.ok(priceListService.getPriceListServices());
    }

    @GetMapping("/services")
    public ResponseEntity<PagedResponse<PriceListResponseDto>> getPriceListServicesPaged(Pageable pageable) {
        return ResponseEntity.ok(priceListService.getPriceListServicesPaged(pageable));
    }

    @GetMapping("/products/all")
    public ResponseEntity<List<PriceListResponseDto>> getPriceListProducts() {
        return ResponseEntity.ok(priceListService.getPriceListProducts());
    }

    @GetMapping("/products")
    public ResponseEntity<PagedResponse<PriceListResponseDto>> getPriceListProductsPaged(Pageable pageable) {
        return ResponseEntity.ok(priceListService.getPriceListProductsPaged(pageable));
    }

    @PatchMapping("/services/{id}")
    public ResponseEntity<PriceListResponseDto> updateServicePrice(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePriceRequestDto updateRequestDto
    ) {
        return ResponseEntity.ok(priceListService.updateService(id, updateRequestDto));
    }

    @PatchMapping("/products/{id}")
    public ResponseEntity<PriceListResponseDto> updateProductPrice(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePriceRequestDto updateRequestDto
    ) {
        return ResponseEntity.ok(priceListService.updateProduct(id, updateRequestDto));
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getPdf() {
        HttpHeaders headers = ResponseHeaderUtils.createPdfHeaders("price_list_report.pdf");
        return new ResponseEntity<>(priceListService.generatePdf(), headers, HttpStatus.OK);
    }
}
