package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentRequestDto;
import com.iss.eventorium.interaction.dtos.comment.UpdateCommentRequestDto;
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

    private final CommentService service;

    @GetMapping("/comments/pending")
    public ResponseEntity<List<CommentResponseDto>> getPendingComments() {
        return ResponseEntity.ok(service.getPendingComments());
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @RequestParam("type") CommentType type,
            @RequestParam("id") Long objectId) {

        List<CommentResponseDto> comments = service.getAcceptedCommentsForTarget(type, objectId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/services/{service-id}/comments")
    public ResponseEntity<CommentResponseDto> createServiceComment(
            @RequestBody @Valid CreateCommentRequestDto request,
            @PathVariable("service-id") Long objectId
    ) {
        return new ResponseEntity<>(service.addComment(objectId, CommentType.SERVICE, request), HttpStatus.CREATED);
    }

    @PostMapping("/products/{product-id}/comments")
    public ResponseEntity<CommentResponseDto> createProductRating(
            @RequestBody @Valid CreateCommentRequestDto request,
            @PathVariable("product-id") Long objectId
    ) {
        return new ResponseEntity<>(service.addComment(objectId, CommentType.PRODUCT, request), HttpStatus.CREATED);
    }

    @PostMapping("/events/{event-id}/comments")
    public ResponseEntity<CommentResponseDto> createEventRating(
            @RequestBody @Valid CreateCommentRequestDto request,
            @PathVariable("event-id") Long objectId
    ) {
        return new ResponseEntity<>(service.addComment(objectId, CommentType.EVENT, request), HttpStatus.CREATED);
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long id, @RequestBody @Valid UpdateCommentRequestDto request) {
        return ResponseEntity.ok(service.updateCommentStatus(id, request.getStatus()));
    }
}