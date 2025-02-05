package com.iss.eventorium.interaction.services;

import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentRequestDto;
import com.iss.eventorium.interaction.mappers.CommentMapper;
import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.interaction.models.CommentType;
import com.iss.eventorium.interaction.repositories.CommentRepository;
import com.iss.eventorium.shared.models.CommentableEntity;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.services.ProductService;
import com.iss.eventorium.solution.services.ServiceService;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.iss.eventorium.interaction.mappers.CommentMapper.toResponse;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final AuthService authService;
    private final ProductService productService;
    private final ServiceService serviceService;;
    private final EventService eventService;

    private final CommentRepository commentRepository;

    public CommentResponseDto createComment(Long id, CommentType type, CreateCommentRequestDto request) {
        CommentableEntity entity = findCommentable(id, type);

        Comment comment = CommentMapper.fromRequest(request, type, id);
        comment.setUser(authService.getCurrentUser());
        entity.addComment(comment);

        commentRepository.save(comment);
        return toResponse(comment, entity);
    }

    public List<CommentResponseDto> getPendingComments() {
        return commentRepository.findByStatus(Status.PENDING).stream()
                .map(c -> toResponse(c, findCommentable(c.getCommentableId(), c.getCommentType())))
                .toList();
    }

    public CommentableEntity findCommentable(Long id, CommentType type) {
        return switch (type) {
            case PRODUCT -> productService.find(id);
            case SERVICE -> serviceService.find(id);
            case EVENT -> eventService.find(id);
        };
    }

    public Comment find(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found!"));
    }

    public CommentResponseDto updateCommentStatus(Long id, Status status) {
        Comment comment = find(id);
        comment.setStatus(status);
        return toResponse(commentRepository.save(comment), findCommentable(comment.getCommentableId(), comment.getCommentType()));
    }
}
