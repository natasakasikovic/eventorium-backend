package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.api.AccountProductApi;
import com.iss.eventorium.solution.dtos.products.ProductFilterDto;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
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
public class AccountProductController implements AccountProductApi {

    private final AccountProductService service;

    @GetMapping("/all")
    public ResponseEntity<List<ProductSummaryResponseDto>> getAllProducts() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> getProductsPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getProductsPaged(pageable));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<List<ProductSummaryResponseDto>> filterAccountProducts(@ModelAttribute ProductFilterDto filter) {
        return ResponseEntity.ok().body(service.filterProducts(filter));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> filterAccountProductsPaged(
            @ModelAttribute ProductFilterDto filter, Pageable pageable) {
        return ResponseEntity.ok().body(service.filterProducts(filter, pageable));
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<ProductSummaryResponseDto>> searchAccountProducts(@RequestParam String keyword) {
        return ResponseEntity.ok().body(service.searchProducts(keyword));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> searchAccountProductsPaged(
            @RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok().body(service.searchProducts(keyword, pageable));
    }

    @GetMapping("/favourites")
    public ResponseEntity<List<ProductSummaryResponseDto>> getFavouriteProducts() {
        return ResponseEntity.ok(service.getFavouriteProducts());
    }

    @GetMapping("/favourites/{id}")
    public ResponseEntity<Boolean> isFavouriteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(service.isFavouriteProduct(id));
    }

    @PostMapping("/favourites/{id}")
    public ResponseEntity<ProductResponseDto> addFavouriteProduct(@PathVariable Long id) {
        return new ResponseEntity<>(service.addFavouriteProduct(id), HttpStatus.CREATED);
    }

    @DeleteMapping("/favourites/{id}")
    public ResponseEntity<Void> removeFavouriteProduct(@PathVariable Long id) {
        service.removeFavouriteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
