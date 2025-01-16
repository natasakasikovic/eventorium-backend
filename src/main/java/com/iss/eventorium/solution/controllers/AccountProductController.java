package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.services.AccountProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account/products")
@RequiredArgsConstructor
public class AccountProductController {

    private final AccountProductService accountProductService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(List.of(new ProductResponseDto()));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponseDto>> getAllProductsPaged() {
        return ResponseEntity.ok(new PagedResponse<>(List.of(new ProductResponseDto()), 1, 100));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<List<ProductSummaryResponseDto>> filterAccountProducts() {
        return ResponseEntity.ok().body(List.of(new ProductSummaryResponseDto()));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> filterAccountProductsPaged(Pageable pageable) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ProductSummaryResponseDto()), 3, 100));
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<ProductSummaryResponseDto>> searchAccountProducts(@RequestParam String keyword) {
        return ResponseEntity.ok().body(List.of(new ProductSummaryResponseDto()));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> searchAccountProductsPaged(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ProductSummaryResponseDto()), 3, 100));
    }

    @GetMapping("/favourites")
    public ResponseEntity<List<ProductSummaryResponseDto>> getFavouriteProducts() {
        return ResponseEntity.ok().body(List.of(new ProductSummaryResponseDto()));
    }

    @GetMapping("/favourites/{id}")
    public ResponseEntity<Boolean> isFavouriteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(accountProductService.isFavouriteProduct(id));
    }

    @PostMapping("/favourites/{id}")
    public ResponseEntity<ProductResponseDto> addFavouriteProduct(@PathVariable Long id) {
        return new ResponseEntity<>(accountProductService.addFavouriteProduct(id), HttpStatus.CREATED);
    }

    @DeleteMapping("/favourites/{id}")
    public ResponseEntity<Void> removeFavouriteProduct(@PathVariable Long id) {
        accountProductService.removeFavouriteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
