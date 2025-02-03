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
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

import static com.iss.eventorium.category.mappers.CategoryMapper.toResponse;


@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class CategoryProposalService {

    private static final String NOTIFICATION_TITLE = "Category";

    private final MessageSource messageSource;

    private final NotificationService notificationService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;

    public CategoryResponseDto updateCategoryStatus(Long categoryId, Status status) {
        Category category = getCategoryProposal(categoryId);
        Service service = getServiceProposal(category);

        if(status == Status.DECLINED) {
            category.setDeleted(true);
            category.setName(Instant.now().toEpochMilli() + "_" + category.getName());
            service.setIsDeleted(true);
        } else {
            category.setSuggested(false);
            service.setCategory(category);
        }

        service.setStatus(status);
        serviceRepository.save(service);

        sendStatusUpdateNotification(category, status, service.getProvider());
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponseDto updateCategoryProposal(Long categoryId, CategoryRequestDto request) {
        Category category = getCategoryProposal(categoryId);

        if (checkCategoryExistence(category, request.getName()))
            throw new CategoryAlreadyExistsException("Category with name " + category.getName() + " already exists!");

        Service service = getServiceProposal(category);
        updateCategoryProposal(category, request);
        Category response = categoryRepository.save(category);

        service.setStatus(Status.ACCEPTED);
        serviceRepository.save(service);

        sendUpdateNotification(category, service.getProvider());
        return toResponse(response);
    }

    public CategoryResponseDto changeCategoryProposal(Long categoryId, CategoryRequestDto request) {
        Category category = getCategoryProposal(categoryId);
        Service service = getServiceProposal(category);

        changeProposal(category, service, request.getName());
        categoryRepository.save(category);
        serviceRepository.save(service);

        sendChangeNotification(category, service, request);
        return toResponse(service.getCategory());
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

    private boolean checkCategoryExistence(Category category, String name) {
        return !Objects.equals(category.getName(), name)
                && categoryRepository.findByName(name).isPresent();
    }

    private void changeProposal(Category category, Solution solution, String newCategoryName) {
        category.setDeleted(true);
        category.setName(Instant.now().toEpochMilli() + "_" + category.getName());
        solution.setStatus(Status.ACCEPTED);
        solution.setCategory(categoryRepository.findByName(newCategoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category with name " + newCategoryName + " not found")));

    }

    private Category getCategoryProposal(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException("Category not found"));
        if(!category.isSuggested()) {
            throw new EntityNotFoundException("Category is not suggested");
        }
        return category;
    }

    private Service getServiceProposal(Category category) {
        Service service = serviceRepository.findByCategoryId(category.getId()).orElseThrow(
                () -> new EntityNotFoundException("Service with category '" + category.getName() + "' not found"));
        if(!service.getStatus().equals(Status.PENDING)) {
            throw new EntityNotFoundException("Service with category '" + category.getName() + "' is not pending");
        }
        return service;
    }

    private String getMessage(Category category, String code) {
        return messageSource.getMessage(
                code,
                new Object[] { category.getName() },
                Locale.getDefault()
        );
    }

    private String getMessage(Category category, CategoryRequestDto dto) {
        return messageSource.getMessage(
                "notification.category.change",
                new Object[] { category.getName(), dto.getName() },
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
