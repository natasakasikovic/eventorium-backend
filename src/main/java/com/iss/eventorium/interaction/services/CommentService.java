package com.iss.eventorium.interaction.services;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.interaction.dtos.comment.CommentInfoDto;
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
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.services.ProductService;
import com.iss.eventorium.solution.services.ServiceService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@org.springframework.stereotype.Service
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
        comment.setId(null);
        comment.setAuthor(authService.getCurrentUser());
        repository.save(comment);
        return mapper.toResponse(comment, getCommentInfo(comment).getDisplayName());
    }

    public List<CommentResponseDto> getPendingComments() {
        Specification<Comment> specification = CommentSpecification.filterPendingComments();
        List<Comment> comments = repository.findAll(specification, Sort.by(Sort.Direction.DESC, "creationDate"));
        return comments.stream().map(comment -> mapper.toResponse(comment, getCommentInfo(comment).getDisplayName())).toList();
    }

    public List<CommentResponseDto> getAcceptedCommentsForTarget(CommentType type, Long objectId) {
        Specification<Comment> specification = CommentSpecification.filterBy(type, objectId, authService.getCurrentUser());
        List<Comment> comments = repository.findAll(specification, Sort.by(Sort.Direction.DESC, "creationDate"));
        return comments.stream().map(comment -> mapper.toResponse(comment, getCommentInfo(comment).getDisplayName())).toList();
    }

    public Comment find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found!"));
    }

    public CommentResponseDto updateCommentStatus(Long id, Status status) {
        Comment comment = find(id);
        comment.setStatus(status);
        CommentInfoDto info = getCommentInfo(comment);

        if (status.equals(Status.ACCEPTED))
            sendNotification(comment.getAuthor(), info.getUser(), info.getDisplayName());

        return mapper.toResponse(repository.save(comment), info.getDisplayName());
    }

    private void sendNotification(User author, User receiver, String displayName) {
        notificationService.sendNotification(receiver, new Notification(
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


    private CommentInfoDto getCommentInfo(Comment comment) {
        Long objectId = comment.getObjectId();
        CommentType type = comment.getCommentType();

        return switch (type) {
            case PRODUCT -> {
                Optional<Product> optionalProduct = productService.findEvenIfDeleted(objectId);
                if (optionalProduct.isPresent()) {
                    Product product = optionalProduct.get();
                    String name = Boolean.TRUE.equals(product.getIsDeleted())
                            ? "[Deleted Product]"
                            : product.getName();
                    yield new CommentInfoDto(name, product.getProvider());
                } else {
                    yield new CommentInfoDto("[Unknown Product]", null);
                }
            }
            case SERVICE -> {
                Optional<Service> optionalService = serviceService.findEvenIfDeleted(objectId);
                if (optionalService.isPresent()) {
                    Service service = optionalService.get();
                    String name = Boolean.TRUE.equals(service.getIsDeleted())
                            ? "[Deleted Service]"
                            : service.getName();
                    yield new CommentInfoDto(name, service.getProvider());
                } else {
                    yield new CommentInfoDto("[Unknown Service]", null);
                }
            }
            case EVENT -> {
                Event event = eventService.find(objectId); // can't be deleted
                yield new CommentInfoDto(event.getName(), event.getOrganizer());
            }
        };
    }
}