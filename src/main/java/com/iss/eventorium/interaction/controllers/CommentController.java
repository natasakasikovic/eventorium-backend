package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.dtos.comment.CreateCommentDto;
import com.iss.eventorium.interaction.dtos.ratings.CreateRatingRequestDto;
import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
import com.iss.eventorium.interaction.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/services/{service-id}/comments")
    public ResponseEntity<RatingResponseDto> createServiceComment(
            @RequestBody @Valid CreateCommentDto createCommentDto,
            @PathVariable("service-id") Long serviceId
    ) {
        return null;
    }

    @PostMapping("/products/{product-id}/comments")
    public ResponseEntity<RatingResponseDto> createProductRating(
            @RequestBody @Valid CreateCommentDto createCommentDto,
            @PathVariable("product-id") Long productId
    ) {
        return null;
    }

    @PostMapping("/events/{event-id}/comments")
    public ResponseEntity<RatingResponseDto> createEventRating(
            @RequestBody @Valid CreateCommentDto createCommentDto,
            @PathVariable("event-id") Long eventId
    ) {
        return null;
    }
}
