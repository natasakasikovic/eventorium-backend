package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.solution.dtos.products.*;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping("/{id}")
    public  ResponseEntity<ProductDetailsDto> getProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getProduct(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductRequestDto createProductRequestDto) {
        return new ResponseEntity<>(service.createProduct(createProductRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("id") Long id, @RequestBody ProductRequestDto productRequestDto) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<Void> deleteImages(@PathVariable("id") Long id, @RequestBody List<RemoveImageRequestDto> removedImages) {
        service.deleteImages(id, removedImages);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> filterProducts(@Valid @ModelAttribute ProductFilterDto filter, Pageable pageable) {
        return  ResponseEntity.ok(service.filter(filter, pageable));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<List<ProductSummaryResponseDto>> filterProducts(@Valid @ModelAttribute ProductFilterDto filter) {
        return  ResponseEntity.ok(service.filter(filter));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> searchProducts(@RequestParam String keyword, Pageable pageable){
        return ResponseEntity.ok(service.search(keyword, pageable));
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<ProductSummaryResponseDto>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<ProductSummaryResponseDto>> getBudgetSuggestions(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("price") Double price
    ) {
        return ResponseEntity.ok(service.getBudgetSuggestions(categoryId, price));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id){
        ImagePath path = service.getImagePath(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(path.getContentType()))
                .body(service.getImage(id, path));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> uploadImages(@PathVariable Long id, @RequestParam("images") List<MultipartFile> images) {
        service.uploadImages(id, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

