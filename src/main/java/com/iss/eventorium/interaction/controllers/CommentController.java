package com.iss.eventorium.interaction.controllers;

import com.iss.eventorium.interaction.api.CommentApi;
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
@RequestMapping("api/v1/comments")
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentService service;

    @GetMapping("/pending")
    public ResponseEntity<List<CommentResponseDto>> getPendingComments() {
        return ResponseEntity.ok(service.getPendingComments());
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @RequestParam("type") CommentType type,
            @RequestParam("id") Long objectId) {

        List<CommentResponseDto> comments = service.getAcceptedCommentsForTarget(type, objectId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestBody @Valid CreateCommentRequestDto request
    ) {
        return new ResponseEntity<>(service.createComment(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long id, @RequestBody @Valid UpdateCommentRequestDto request) {
        return ResponseEntity.ok(service.updateCommentStatus(id, request.getStatus()));
    }
}