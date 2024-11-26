package com.iss.eventorium.product.controllers;

import com.iss.eventorium.product.dtos.ProductSummaryResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

