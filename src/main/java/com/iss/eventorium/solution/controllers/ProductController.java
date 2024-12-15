package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.EventTypeResponseDto;
import com.iss.eventorium.solution.dtos.products.CreateProductRequestDto;
import com.iss.eventorium.solution.dtos.products.ProductRequestDto;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.shared.utils.ProductFilter;
import com.iss.eventorium.solution.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    @Autowired  //TODO: when products attribute is deleted, remove this annotation and do it as in other controllers
    private ProductService service;

    private final List<ProductResponseDto> products;

    public ProductController() {
        ProductResponseDto product1 = new ProductResponseDto(
                1L,
                "Wedding Package A",
                "A premium wedding package with full services.",
                "Luxury venue, gourmet catering, premium decor",
                5000.00,
                10.0,
                Status.PENDING,
                LocalDateTime.of(2024, 1, 15, 9, 0),
                true,
                true,
                List.of(),
                new CategoryResponseDto()
        );

        ProductResponseDto product2 = new ProductResponseDto(
                2L,
                "Corporate Conference Kit",
                "Everything you need for a corporate event.",
                "Custom branding, audio-visual equipment, team building",
                1500.00,
                15.0,
                Status.DECLINED,
                LocalDateTime.of(2024, 3, 10, 8, 0),
                true,
                true,
                List.of(new EventTypeResponseDto()),
                new CategoryResponseDto()
        );

        ProductResponseDto product3 = new ProductResponseDto(
                3L,
                "Outdoor Party Set",
                "Outdoor event setup with furniture and lighting.",
                "Tents, lounge seating, party lights",
                800.00,
                5.0,
                Status.ACCEPTED,
                LocalDateTime.of(2024, 6, 1, 12, 0),
                false,
                false,
                List.of(new EventTypeResponseDto()),
                new CategoryResponseDto()
        );
        this.products = List.of(product1, product2, product3);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ProductResponseDto> getProduct(@PathVariable("id") Long id) {
        // ProductResponseDto product = productsService.get(id);
        // TODO: uncomment line above once service is implemented and also delete the expression below

        ProductResponseDto product = products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (product == null) { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody CreateProductRequestDto createProductRequestDto) {
        ProductResponseDto responseDto = new ProductResponseDto();
        // TODO: call service and map
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("id") Long id, @RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto product = products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (product == null) { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }

        // TODO: call service and map
        return new ResponseEntity<>(product, HttpStatus.OK);
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
        // TODO: call service -> productService.getAll();
        Collection<ProductSummaryResponseDto> products = List.of( new ProductSummaryResponseDto(1L, "Product 1", 4.5, true, true) ,
                new ProductSummaryResponseDto(2L, "Product 2", 5.4, true, true));
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductSummaryResponseDto>> getProductsPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getProducts(pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Collection<ProductSummaryResponseDto>> filterProducts(
            ProductFilter filter,
            Pageable pageable
    ) {
        Collection<ProductSummaryResponseDto> filteredProducts =  // TODO: delete 2 lines below and change with -> service.filterProducts(..params)
                List.of( new ProductSummaryResponseDto(1L, "Balloons", 85.0, true, true),
                        new ProductSummaryResponseDto(2L, "Product", 5.7, true, true));
        return new ResponseEntity<>(filteredProducts, HttpStatus.OK);
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

