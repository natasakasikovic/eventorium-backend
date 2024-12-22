package com.iss.eventorium.category.services;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import com.iss.eventorium.interaction.models.Notification;
import com.iss.eventorium.interaction.models.NotificationType;
import com.iss.eventorium.interaction.services.NotificationService;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import static com.iss.eventorium.category.mappers.CategoryMapper.toResponse;


@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class CategoryProposalService {

    private final NotificationService notificationService;

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;

    public CategoryResponseDto updateCategoryStatus(Long categoryId, Status status) {
        Category category = getCategoryProposal(categoryId);
        Service service = getServiceProposal(category);

        Notification notification;
        service.setStatus(status);
        category.setSuggested(false);
        if(status == Status.DECLINED) {
            category.setDeleted(true);
            notification = new Notification(
                    "Unfortunately, your category suggestion (" + category.getName() + ") has been declined.",
                    NotificationType.DECLINED
            );
        } else {
            notification = new Notification(
                    "Your category suggestion (" + category.getName() + ") has been accepted.",
                    NotificationType.ACCEPTED
            );
        }

        categoryRepository.save(category);
        service.setCategory(category);
        serviceRepository.save(service);

        notificationService.sendNotification(service.getProvider().getId(), notification);

        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponseDto updateCategoryProposal(Long categoryId, CategoryRequestDto dto) {
        Category category = getCategoryProposal(categoryId);
        Service service = getServiceProposal(category);

        category.setSuggested(false);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        Category response = categoryRepository.save(category);
        service.setStatus(Status.ACCEPTED);
        serviceRepository.save(service);

        Notification notification = new Notification(
                "Your category suggestion (" + category.getName() + ") has been accepted, but the details have been updated.",
                NotificationType.ACCEPTED
        );
        notificationService.sendNotification(service.getProvider().getId(), notification);

        return toResponse(response);
    }

    public CategoryResponseDto changeCategoryProposal(Long categoryId, CategoryRequestDto dto) {
        Category category = getCategoryProposal(categoryId);
        Service service = getServiceProposal(category);

        category.setSuggested(false);
        category.setDeleted(true);
        service.setStatus(Status.ACCEPTED);
        service.setCategory(categoryRepository.findByName(dto.getName())
                .orElseThrow(() -> new EntityNotFoundException("Category with name " + dto.getName() + " not found")));

        Notification notification = new Notification(
                "Your category suggestion (" + category.getName() + ") has been changed to " + dto.getName() + ".",
                NotificationType.INFO
        );

        notificationService.sendNotification(service.getProvider().getId(), notification);

        categoryRepository.save(category);
        serviceRepository.save(service);
        return toResponse(service.getCategory());
    }

    private Category getCategoryProposal(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException("Category with id " + categoryId + " not found"));
        if(!category.isSuggested()) {
            throw new EntityNotFoundException("Category with id " + categoryId + " is not suggested");
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
}
