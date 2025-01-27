package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.dtos.review.CreateReviewRequestDto;
import com.iss.eventorium.interaction.dtos.review.ReviewResponseDto;
import com.iss.eventorium.interaction.dtos.review.UpdateReviewRequestDto;
import com.iss.eventorium.interaction.models.Review;
import com.iss.eventorium.interaction.services.ReviewService;
import com.iss.eventorium.shared.models.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/services/{service-id}/reviews/all")
    public ResponseEntity<List<Review>> getServiceReviews(@PathVariable("service-id") Long serviceId) {
        return ResponseEntity.ok(List.of(new Review(), new Review()));
    }

    @GetMapping("/services/{service-id}/reviews")
    public ResponseEntity<PagedResponse<Review>> getServiceReviewsPaged(
            @PathVariable("service-id") Long serviceId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedResponse<>(List.of(new Review()), 2, 100));
    }

    @GetMapping("/products/{product-id}/reviews/all")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable("product-id") Long productId) {
        return ResponseEntity.ok(List.of(new Review(), new Review()));
    }

    @GetMapping("/products/{product-id}/reviews")
    public ResponseEntity<PagedResponse<Review>> getProductReviewsPaged(
            @PathVariable("product-id") Long productId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedResponse<>(List.of(new Review()), 2, 100));
    }

    @GetMapping("/reviews/pending/all")
    public ResponseEntity<List<ReviewResponseDto>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    @PostMapping("/services/{service-id}/reviews")
    public ResponseEntity<ReviewResponseDto> createServiceReview(
            @RequestBody @Valid CreateReviewRequestDto createReviewRequestDto,
            @PathVariable("service-id") Long serviceId
    ) {
        return new ResponseEntity<>(reviewService.createServiceReview(serviceId, createReviewRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/products/{product-id}/reviews")
    public ResponseEntity<ReviewResponseDto> createProductReview(
            @RequestBody @Valid CreateReviewRequestDto createReviewRequestDto,
            @PathVariable("product-id") Long productId
    ) {
        return new ResponseEntity<>(reviewService.createProductReview(productId, createReviewRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(@RequestBody @Valid UpdateReviewRequestDto status, @PathVariable Long id) {
        return ResponseEntity.ok(reviewService.updateReview(id, status.getStatus()));
    }

}