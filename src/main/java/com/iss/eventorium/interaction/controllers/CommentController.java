package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentDto;
import com.iss.eventorium.interaction.dtos.ratings.CreateRatingRequestDto;
import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
import com.iss.eventorium.interaction.models.CommentType;
import com.iss.eventorium.interaction.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @GetMapping("/comments/pending")
    public ResponseEntity<List<CommentResponseDto>> getPendingComments() {
        return ResponseEntity.ok(commentService.getPendingComments());
    }

    @PostMapping("/services/{service-id}/comments")
    public ResponseEntity<CommentResponseDto> createServiceComment(
            @RequestBody @Valid CreateCommentDto request,
            @PathVariable("service-id") Long id
    ) {
        return new ResponseEntity<>(commentService.createComment(id, CommentType.SERVICE, request), HttpStatus.CREATED);
    }

    @PostMapping("/products/{product-id}/comments")
    public ResponseEntity<CommentResponseDto> createProductRating(
            @RequestBody @Valid CreateCommentDto request,
            @PathVariable("product-id") Long id
    ) {
        return new ResponseEntity<>(commentService.createComment(id, CommentType.PRODUCT, request), HttpStatus.CREATED);
    }

    @PostMapping("/events/{event-id}/comments")
    public ResponseEntity<CommentResponseDto> createEventRating(
            @RequestBody @Valid CreateCommentDto request,
            @PathVariable("event-id") Long id
    ) {
        return new ResponseEntity<>(commentService.createComment(id, CommentType.EVENT, request), HttpStatus.CREATED);
    }
}
