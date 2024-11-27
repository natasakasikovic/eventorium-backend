package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.dtos.CreateRatingRequestDto;
import com.iss.eventorium.interaction.dtos.RatingResponseDto;
import com.iss.eventorium.interaction.mappers.RatingMapper;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/v1")
public class RatingController {

    @GetMapping("/services/{service-id}/ratings/all")
    public ResponseEntity<List<Rating>> getServiceRatings(@PathVariable("service-id") Long serviceId) {
        return ResponseEntity.ok(List.of(new Rating(), new Rating()));
    }

    @GetMapping("/services/{service-id}/ratings")
    public ResponseEntity<PagedResponse<Rating>> getServiceRatingsPaged(
            @PathVariable("service-id") Long serviceId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedResponse<>(List.of(new Rating()), 2, 100));
    }

    @GetMapping("/products/{product-id}/ratings/all")
    public ResponseEntity<List<Rating>> getProductRatings(@PathVariable("product-id") Long productId) {
        return ResponseEntity.ok(List.of(new Rating(), new Rating()));
    }

    @GetMapping("/products/{product-id}/ratings")
    public ResponseEntity<PagedResponse<Rating>> getProductRatingsPaged(
            @PathVariable("product-id") Long productId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedResponse<>(List.of(new Rating()), 2, 100));
    }

    @PostMapping("/services/{service-id}/ratings")
    public ResponseEntity<RatingResponseDto> createServiceRating(
            @RequestBody CreateRatingRequestDto createRatingRequestDto,
            @PathVariable("service-id") String serviceId
    ) {
        Rating rating = RatingMapper.fromCreateRequest(createRatingRequestDto);
        rating.setId(100L);
        rating.setCreationDate(LocalDateTime.now());
        return new ResponseEntity<>(RatingMapper.toResponse(rating), HttpStatus.CREATED);
    }

    @PostMapping("/products/{product-id}/ratings")
    public ResponseEntity<RatingResponseDto> createProductRating(
            @RequestBody CreateRatingRequestDto createRatingRequestDto,
            @PathVariable("product-id") String productId
    ) {
        Rating rating = RatingMapper.fromCreateRequest(createRatingRequestDto);
        rating.setId(101L);
        rating.setCreationDate(LocalDateTime.now());
        return new ResponseEntity<>(RatingMapper.toResponse(rating), HttpStatus.CREATED);
    }
}
