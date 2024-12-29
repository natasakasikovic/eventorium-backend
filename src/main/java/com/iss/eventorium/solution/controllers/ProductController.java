package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.solution.dtos.products.CreateProductRequestDto;
import com.iss.eventorium.solution.dtos.products.ProductRequestDto;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.shared.utils.ProductFilter;
import com.iss.eventorium.solution.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping("/{id}")
    public  ResponseEntity<ProductResponseDto> getProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getProduct(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody CreateProductRequestDto createProductRequestDto) {
        ProductResponseDto responseDto = new ProductResponseDto();
        // TODO: call service and map
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("id") Long id, @RequestBody ProductRequestDto productRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top-five-products")
    public ResponseEntity<Collection<ProductSummaryResponseDto>> getTopProducts(){
        return ResponseEntity.ok(service.getTopProducts());
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ProductSummaryResponseDto>> getProducts() {
        return ResponseEntity.ok(service.getProducts());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> getProductsPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getProducts(pageable));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ImageResponseDto>> getImages(@PathVariable Long id) {
        return ResponseEntity.ok(service.getImages(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<Collection<ProductSummaryResponseDto>> filterProducts(
            ProductFilter filter,
            Pageable pageable
    ) {
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> searchProducts(@RequestParam String keyword, Pageable pageable){
        return ResponseEntity.ok(service.search(keyword, pageable));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<ProductSummaryResponseDto>> getBudgetSuggestions(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("price") Double price
    ) {
        return ResponseEntity.ok(service.getBudgetSuggestions(categoryId, price));
    }

}

