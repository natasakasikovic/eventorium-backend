package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.dtos.CreateReviewRequestDto;
import com.iss.eventorium.interaction.dtos.ReviewResponseDto;
import com.iss.eventorium.interaction.dtos.UpdateReviewDto;
import com.iss.eventorium.interaction.dtos.UpdatedReviewDto;
import com.iss.eventorium.interaction.mappers.ReviewMapper;
import com.iss.eventorium.interaction.models.Review;
import com.iss.eventorium.shared.models.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1")
public class ReviewController {

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

    @GetMapping("/products/{product-id}/review/all")
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

    @PostMapping("/services/{service-id}/reviews")
    public ResponseEntity<ReviewResponseDto> createServiceReview(
            @RequestBody CreateReviewRequestDto createReviewRequestDto,
            @PathVariable("service-id") String serviceId
    ) {
        Review review = ReviewMapper.fromCreateRequest(createReviewRequestDto);
        review.setId(100L);
        review.setCreationDate(LocalDateTime.now());
        return new ResponseEntity<>(ReviewMapper.toResponse(review), HttpStatus.CREATED);
    }

    @PostMapping("/products/{product-id}/reviews")
    public ResponseEntity<ReviewResponseDto> createProductReview(
            @RequestBody CreateReviewRequestDto createReviewRequestDto,
            @PathVariable("product-id") String productId
    ) {
        Review review = ReviewMapper.fromCreateRequest(createReviewRequestDto);
        review.setId(101L);
        review.setCreationDate(LocalDateTime.now());
        return new ResponseEntity<>(ReviewMapper.toResponse(review), HttpStatus.CREATED);
    }

    @PutMapping("/products/{product-id}/reviews/{review-id}")
    public ResponseEntity<UpdatedReviewDto> updateProductReview(
            @PathVariable("product-id") Long productId,
            @PathVariable("review-id") Long reviewId,
            @RequestBody UpdateReviewDto review) throws Exception {
        // TODO: Call -> reviewService.updateReview(productId, reviewId, review);
        UpdatedReviewDto updatedReview = new UpdatedReviewDto();
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @PutMapping("/services/{service-id}/reviews/{review-id}")
    public ResponseEntity<UpdatedReviewDto> updateServiceReview(
            @PathVariable("service-id") Long serviceId,
            @PathVariable("review-id") Long reviewId,
            @RequestBody UpdateReviewDto review) throws Exception {
        // TODO: Call -> reviewService.updateReview(serviceId, reviewId, review);
        UpdatedReviewDto updatedReview = new UpdatedReviewDto();
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }


    @DeleteMapping("/products/{product-id}/reviews/{review-id}")
    public ResponseEntity<?> deleteProductReview(
            @PathVariable("product-id") Long productId,
            @PathVariable("review-id") Long reviewId) {
        // TODO: call service -> reviewService.delete(productId, reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/services/{service-id}/reviews/{review-id}")
    public ResponseEntity<?> deleteServiceReview(
            @PathVariable("service-id") Long serviceId,
            @PathVariable("review-id") Long reviewId) {
        // TODO: call service -> reviewService.delete(serviceId, reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}