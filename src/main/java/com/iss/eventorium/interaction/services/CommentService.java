package com.iss.eventorium.interaction.services;

import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentDto;
import com.iss.eventorium.interaction.mappers.CommentMapper;
import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.interaction.models.CommentType;
import com.iss.eventorium.interaction.repositories.CommentRepository;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final AuthService authService;

    private final CommentRepository commentRepository;

    public Comment createComment(Long id, CommentType type, CreateCommentDto request) {
        Comment comment = CommentMapper.fromRequest(request);
        comment.setUser(authService.getCurrentUser());
        comment.setCommentType(type);
        commentRepository.save(comment);
        return comment;
    }

}
