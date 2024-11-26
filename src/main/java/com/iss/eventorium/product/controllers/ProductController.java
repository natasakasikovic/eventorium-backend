package com.iss.eventorium.product.controllers;

import com.iss.eventorium.product.dtos.ProductSummaryResponseDto;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.shared.utils.ProductFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    @GetMapping("/top-five-products")
    public ResponseEntity<Collection<ProductSummaryResponseDto>> getTopProducts(){
        //Collection<ProductSummaryResponseDto> topProducts = productsService.getTopProducts();
        // TODO: uncomment line above once service is implemented and also delete data below

        Collection<ProductSummaryResponseDto> topProducts = List.of(
                new ProductSummaryResponseDto(1L, "Product 1", 4.5, true, true),
                new ProductSummaryResponseDto(2L, "Product 2", 4.3, true, true),
                new ProductSummaryResponseDto(3L, "Product 3", 4.0, true, true),
                new ProductSummaryResponseDto(4L, "Product 4", 3.9, true, true),
                new ProductSummaryResponseDto(5L, "Product 5", 3.8, true, true)
        );

        return new ResponseEntity<>(topProducts, HttpStatus.OK);
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
        // TODO: call service -> productService.get(pageable)
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ProductSummaryResponseDto(1L, "Product", 8.5, true, true)), 1, 3));
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
    public ResponseEntity<Collection<ProductSummaryResponseDto>> searchProducts(@RequestParam String keyword, Pageable pageable){
        Collection<ProductSummaryResponseDto> products = List.of( // TODO: delete 2 lines below and change with -> service.searchProducts(keyword, pagable);
                new ProductSummaryResponseDto(1L, "Product 1", 4.5, true, true),
                new ProductSummaryResponseDto(2L, "Product 2", 4.5, true, true));
        return ResponseEntity.ok(products);
    }

}

