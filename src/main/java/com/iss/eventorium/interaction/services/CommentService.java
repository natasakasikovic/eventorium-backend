package com.iss.eventorium.interaction.services;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentRequestDto;
import com.iss.eventorium.interaction.mappers.CommentMapper;
import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.interaction.models.CommentType;
import com.iss.eventorium.interaction.repositories.CommentRepository;
import com.iss.eventorium.notifications.models.Notification;
import com.iss.eventorium.notifications.models.NotificationType;
import com.iss.eventorium.notifications.services.NotificationService;
import com.iss.eventorium.shared.models.CommentableEntity;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.services.ProductService;
import com.iss.eventorium.solution.services.ServiceService;
import com.iss.eventorium.solution.services.SolutionService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

import static com.iss.eventorium.interaction.mappers.CommentMapper.toResponse;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final AuthService authService;
    private final ProductService productService;
    private final ServiceService serviceService;
    private final SolutionService solutionService;
    private final EventService eventService;
    private final NotificationService notificationService;

    private final CommentRepository commentRepository;

    private final MessageSource messageSource;

    public CommentResponseDto createComment(Long id, CommentType type, CreateCommentRequestDto request) {
        CommentableEntity entity = findCommentable(id, type);

        Comment comment = CommentMapper.fromRequest(request, type, id);
        comment.setUser(authService.getCurrentUser());
        addComment(entity, comment, type);

        return toResponse(comment, entity);
    }

    public List<CommentResponseDto> getPendingComments() {
        return commentRepository.findByStatusOrderByCreationDateDesc(Status.PENDING).stream()
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
        CommentableEntity commentable = findCommentable(comment.getCommentableId(), comment.getCommentType());
        comment.setStatus(status);

        if(status.equals(Status.ACCEPTED)) {
            sendNotification(comment.getUser(), commentable);
        }

        return toResponse(commentRepository.save(comment), commentable);
    }

    private void addComment(CommentableEntity entity, Comment comment, CommentType type) {
        if(!type.equals(CommentType.EVENT)) {
            solutionService.addComment((Solution) entity, comment);
        } else {
            eventService.addComment((Event) entity, comment);
        }
    }

    private String getMessage(User user, CommentableEntity entity) {
        String person = user.getPerson().getName() + " " + user.getPerson().getLastname();
        return messageSource.getMessage(
                "notification.comment",
                new Object[] { person, entity.getDisplayName() },
                Locale.getDefault()
        );
    }


    private void sendNotification(User user, CommentableEntity entity) {
        notificationService.sendNotification(entity.getCreator(), new Notification(
                "Comment",
                getMessage(user, entity),
                NotificationType.INFO
        ));
    }
}
