package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.dtos.review.CreateReviewRequestDto;
import com.iss.eventorium.interaction.dtos.review.ReviewResponseDto;
import com.iss.eventorium.interaction.mappers.ReviewMapper;
import com.iss.eventorium.interaction.models.Review;
import com.iss.eventorium.interaction.repositories.ReviewRepository;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ProductRepository;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ServiceRepository serviceRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    public ReviewResponseDto createServiceReview(Long serviceId, CreateReviewRequestDto createReviewRequestDto) {
        Review review = ReviewMapper.fromCreateRequest(createReviewRequestDto);
        review.setUser(authService.getCurrentUser());
        Service service = serviceRepository.findById(serviceId).orElseThrow(
                () -> new EntityNotFoundException("Service with id " + serviceId + " not found")
        );
        service.addReview(review);
        serviceRepository.save(service);
        return ReviewMapper.toResponse(review);
    }

    public ReviewResponseDto createProductReview(Long productId, CreateReviewRequestDto createReviewRequestDto) {
        Review review = ReviewMapper.fromCreateRequest(createReviewRequestDto);
        review.setUser(authService.getCurrentUser());
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + productId + " not found")
        );
        product.addReview(review);
        productRepository.save(product);
        return ReviewMapper.toResponse(review);
    }

    public List<ReviewResponseDto> getPendingReviews() {
        return reviewRepository.findByStatus(Status.PENDING).stream().map(ReviewMapper::toResponse).toList();
    }

    public ReviewResponseDto updateReview(Long id, Status status) {
        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Review not found")
        );
        review.setStatus(status);
        reviewRepository.save(review);
        return ReviewMapper.toResponse(review);
    }
}
