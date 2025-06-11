package com.iss.eventorium.interaction.services;

import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentRequestDto;
import com.iss.eventorium.interaction.mappers.CommentMapper;
import com.iss.eventorium.interaction.models.Comment;
import com.iss.eventorium.interaction.models.CommentType;
import com.iss.eventorium.interaction.repositories.CommentRepository;
import com.iss.eventorium.interaction.specifications.CommentSpecification;
import com.iss.eventorium.notifications.models.Notification;
import com.iss.eventorium.notifications.models.NotificationType;
import com.iss.eventorium.notifications.services.NotificationService;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.services.ProductService;
import com.iss.eventorium.solution.services.ServiceService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final AuthService authService;
    private final ProductService productService;
    private final ServiceService serviceService;
    private final EventService eventService;
    private final NotificationService notificationService;

    private final CommentRepository repository;

    private final CommentMapper mapper;

    private final MessageSource messageSource;

    public CommentResponseDto createComment(CreateCommentRequestDto request) {
        Comment comment = mapper.fromRequest(request);
        comment.setAuthor(authService.getCurrentUser());
        repository.save(comment);
        return mapper.toResponse(comment, getDisplayName(comment));
    }

    public List<CommentResponseDto> getPendingComments() {
        Specification<Comment> specification = CommentSpecification.filterPendingComments();
        List<Comment> comments = repository.findAll(specification, Sort.by(Sort.Direction.DESC, "creationDate"));
        return comments.stream().map(comment -> mapper.toResponse(comment, getDisplayName(comment))).toList();
    }

    public List<CommentResponseDto> getAcceptedCommentsForTarget(CommentType type, Long objectId) {
        Specification<Comment> specification = CommentSpecification.filterBy(type, objectId, authService.getCurrentUser());
        List<Comment> comments = repository.findAll(specification, Sort.by(Sort.Direction.DESC, "creationDate"));
        return comments.stream().map(comment -> mapper.toResponse(comment, getDisplayName(comment))).toList();
    }

    public Comment find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found!"));
    }

    public CommentResponseDto updateCommentStatus(Long id, Status status) {
        Comment comment = find(id);
        comment.setStatus(status);
        String displayName = getDisplayName(comment);

        if (status.equals(Status.ACCEPTED))
            sendNotification(comment.getAuthor(), displayName);

        return mapper.toResponse(repository.save(comment), displayName);
    }

    private void sendNotification(User author, String displayName) {
        notificationService.sendNotification(author, new Notification(
                "Comment",
                getMessage(author, displayName),
                NotificationType.INFO
        ));
    }

    private String getMessage(User user, String displayName) {
        String person = user.getPerson().getName() + " " + user.getPerson().getLastname();
        return messageSource.getMessage(
                "notification.comment",
                new Object[] { person, displayName },
                Locale.getDefault()
        );
    }

    private String getDisplayName(Comment comment) { // display name is name of obj that needs to be shown in table
        Long objectId = comment.getObjectId();
        CommentType type = comment.getCommentType();

        return switch (type) {
            case PRODUCT -> productService.find(objectId).getName();
            case SERVICE -> serviceService.find(objectId).getName();
            case EVENT -> eventService.find(objectId).getName();
        };
    }
}