package com.iss.eventorium.category.services;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.exceptions.CategoryAlreadyExistsException;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import com.iss.eventorium.notifications.models.Notification;
import com.iss.eventorium.notifications.models.NotificationType;
import com.iss.eventorium.notifications.services.NotificationService;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.services.SolutionService;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;

import static com.iss.eventorium.category.mappers.CategoryMapper.toResponse;


@RequiredArgsConstructor
@Service
public class CategoryProposalService {

    private static final String NOTIFICATION_TITLE = "Category";

    private final NotificationService notificationService;
    private final CategoryService categoryService;
    private final SolutionService solutionService;

    private final CategoryRepository categoryRepository;

    private final MessageSource messageSource;

    public CategoryResponseDto updateCategoryStatus(Long categoryId, Status status) {
        Category category = getCategoryProposal(categoryId);
        Solution solution = getSolutionProposal(category);

        if(status == Status.DECLINED) {
            category.setDeleted(true);
            category.setName(Instant.now().toEpochMilli() + "_" + category.getName());
            solution.setIsDeleted(true);
        } else {
            category.setSuggested(false);
            solution.setCategory(category);
        }

        solutionService.saveStatus(solution, status);
        sendStatusUpdateNotification(category, status, solution.getProvider());
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponseDto updateCategoryProposal(Long categoryId, CategoryRequestDto request) {
        Category category = getCategoryProposal(categoryId);

        if (categoryService.checkCategoryExistence(category, request.getName()))
            throw new CategoryAlreadyExistsException("Category with name " + category.getName() + " already exists!");

        Solution solution = getSolutionProposal(category);
        updateCategoryProposal(category, request);
        Category response = categoryRepository.save(category);

        solutionService.saveStatus(solution, Status.ACCEPTED);

        sendUpdateNotification(category, solution.getProvider());
        return toResponse(response);
    }

    public CategoryResponseDto changeCategoryProposal(Long categoryId, CategoryRequestDto request) {
        Category category = getCategoryProposal(categoryId);
        Solution solution = getSolutionProposal(category);

        changeProposal(category);
        categoryRepository.save(category);
        solutionService.setCategory(solution, categoryService.findByName(request.getName()));

        sendChangeNotification(category, solution, request);
        return toResponse(solution.getCategory());
    }

    public void handleCategoryProposal(Category category) {
        categoryService.ensureCategoryNameIsUnique(category);
        category.setSuggested(true);
        sendNotificationToAdmin(category);
    }

    private void updateCategoryProposal(Category category, CategoryRequestDto request) {
        category.setSuggested(false);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
    }

    private void sendChangeNotification(Category category, Solution solution, CategoryRequestDto request) {
        Notification notification = new Notification(
                NOTIFICATION_TITLE,
                getMessage(category, request),
                NotificationType.INFO
        );
        notificationService.sendNotification(solution.getProvider(), notification);
    }

    private void sendUpdateNotification(Category category, User provider) {
        Notification notification = new Notification(
                NOTIFICATION_TITLE,
                getMessage(category, "notification.category.updated"),
                NotificationType.SUCCESS
        );
        notificationService.sendNotification(provider, notification);
    }

    private void sendStatusUpdateNotification(Category category, Status status, User provider) {
        Notification notification;
        if(status == Status.DECLINED) {
            notification = new Notification(NOTIFICATION_TITLE,
                    getMessage(category, "notification.category.declined"),
                    NotificationType.INFO
            );
        } else {
            notification = new Notification(
                    NOTIFICATION_TITLE,
                    getMessage(category, "notification.category.accepted"),
                    NotificationType.SUCCESS
            );
        }
        notificationService.sendNotification(provider, notification);
    }

    private void changeProposal(Category category) {
        category.setDeleted(true);
        category.setName(Instant.now().toEpochMilli() + "_" + category.getName());
    }

    private Category getCategoryProposal(Long categoryId) {
        Category category = categoryService.find(categoryId);
        if(!category.isSuggested()) {
            throw new EntityNotFoundException("Category is not suggested");
        }
        return category;
    }

    private Solution getSolutionProposal(Category category) {
        Solution solution = solutionService.findSolutionByCategory(category);
        if(!solution.getStatus().equals(Status.PENDING)) {
            throw new EntityNotFoundException("Solution with category '" + category.getName() + "' is not pending");
        }
        return solution;
    }

    private String getMessage(Category category, String code) {
        return messageSource.getMessage(
                code,
                new Object[] { category.getName() },
                Locale.getDefault()
        );
    }

    private String getMessage(Category category, CategoryRequestDto request) {
        return messageSource.getMessage(
                "notification.category.change",
                new Object[] { category.getName(), request.getName() },
                Locale.getDefault()
        );
    }

    private void sendNotificationToAdmin(Category category) {
        Notification notification = new Notification(
                "Category proposal",
                messageSource.getMessage(
                        "notification.category.proposal",
                        new Object[] { category.getName() },
                        Locale.getDefault()
                ),
                NotificationType.INFO
        );
        notificationService.sendNotificationToAdmin(notification);
    }
}
