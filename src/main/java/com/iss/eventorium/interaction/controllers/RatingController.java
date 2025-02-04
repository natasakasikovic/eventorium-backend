package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.dtos.ratings.CreateRatingRequestDto;
import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
import com.iss.eventorium.interaction.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;


    @PostMapping("/services/{service-id}/ratings")
    public ResponseEntity<RatingResponseDto> createServiceRating(
            @RequestBody @Valid CreateRatingRequestDto createRatingRequestDto,
            @PathVariable("service-id") Long serviceId
    ) {
        return new ResponseEntity<>(ratingService.createRating(serviceId, createRatingRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/products/{product-id}/ratings")
    public ResponseEntity<RatingResponseDto> createProductRating(
            @RequestBody @Valid CreateRatingRequestDto createRatingRequestDto,
            @PathVariable("product-id") Long productId
    ) {
        return new ResponseEntity<>(ratingService.createRating(productId, createRatingRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/events/{event-id}/ratings")
    public ResponseEntity<RatingResponseDto> createEventRating(
            @RequestBody @Valid CreateRatingRequestDto createRatingRequestDto,
            @PathVariable("event-id") Long eventId
    ) {
        return new ResponseEntity<>(ratingService.createEventRating(eventId, createRatingRequestDto), HttpStatus.CREATED);
    }

}