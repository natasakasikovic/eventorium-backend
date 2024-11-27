package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/products")
public class ProfileProductController {

    @GetMapping("/favourites/all")
    public ResponseEntity<List<ProductSummaryResponseDto>> getFavouriteProducts() {
        return ResponseEntity.ok().body(List.of(new ProductSummaryResponseDto()));
    }

    @GetMapping("/favourites")
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> getFavouriteProductsPaged(Pageable pageable) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ProductSummaryResponseDto()), 3, 100));
    }

    @GetMapping("/favourites/{id}")
    public ResponseEntity<ProductResponseDto> getFavouriteProduct(@PathVariable Long id) {
        return ResponseEntity.ok().body(new ProductResponseDto());
    }

    @PutMapping("/favourites/{id}")
    public ResponseEntity<ProductResponseDto> addFavouriteProduct(@PathVariable Long id) {
        return ResponseEntity.ok().body(new ProductResponseDto());
    }

    @DeleteMapping("/favourites/{id}")
    public ResponseEntity<Void> removeFavouriteProduct(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
