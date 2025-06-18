package com.iss.eventorium.category.services;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.exceptions.CategoryAlreadyExistsException;
import com.iss.eventorium.category.exceptions.CategoryInUseException;
import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import com.iss.eventorium.notifications.models.Notification;
import com.iss.eventorium.notifications.models.NotificationType;
import com.iss.eventorium.notifications.services.NotificationService;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.services.SolutionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final SolutionService solutionService;
    private final NotificationService notificationService;

    private final CategoryRepository categoryRepository;

    private final CategoryMapper mapper;

    private final MessageSource messageSource;

    private static final String CATEGORY_ALREADY_EXISTS_MESSAGE = "Category with name %s already exists!";

    public List<CategoryResponseDto> getCategories() {
        return categoryRepository.findBySuggestedFalse().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PagedResponse<CategoryResponseDto> getCategoriesPaged(Pageable pageable) {
        return mapper.toPagedResponse(categoryRepository.findBySuggestedFalse(pageable));
    }

    public CategoryResponseDto getCategory(Long id) {
        return mapper.toResponse(find(id));
    }

    public CategoryResponseDto createCategory(CategoryRequestDto category) {
        ensureCategoryNameIsUnique(category.getName());
        Category created = mapper.fromRequest(category);
        created.setSuggested(false);
        return mapper.toResponse(categoryRepository.save(created));
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto request) {
        Category toUpdate = find(id);
        String oldName = toUpdate.getName();

        ensureCategoryNameAvailability(toUpdate, request.getName());

        toUpdate.setName(request.getName());
        toUpdate.setDescription(request.getDescription());
        CategoryResponseDto response = mapper.toResponse(categoryRepository.save(toUpdate));

        if(!oldName.equals(request.getName()))
            sendCategoryUpdateNotification(oldName, toUpdate.getName());

        return response;
    }

    public void deleteCategory(Long id) {
        Category toDelete = find(id);

        if(solutionService.existsCategory(id))
            throw new CategoryInUseException("Unable to delete category because it is currently associated with an active solution.");
        toDelete.setName(Instant.now().toEpochMilli() + "_" + toDelete.getName());
        toDelete.setDeleted(true);
        categoryRepository.save(toDelete);
    }

    public List<CategoryResponseDto> getPendingCategories() {
        return categoryRepository.findBySuggestedTrue().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PagedResponse<CategoryResponseDto> getPendingCategoriesPaged(Pageable pageable) {
        return mapper.toPagedResponse(categoryRepository.findBySuggestedTrue(pageable));
    }

    public void ensureCategoryNameIsUnique(String categoryName) {
        if (categoryRepository.existsByNameIgnoreCase(categoryName))
            throw new CategoryAlreadyExistsException(String.format(CATEGORY_ALREADY_EXISTS_MESSAGE, categoryName));
    }

    public Category find(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public Category findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException("Category with name '" + name + "' not found"));
    }

    public void ensureCategoryNameAvailability(Category category, String name) {
        if(!Objects.equals(category.getName(), name) && categoryRepository.existsByNameIgnoreCase(name))
            throw new CategoryAlreadyExistsException(String.format(CATEGORY_ALREADY_EXISTS_MESSAGE, category.getName()));
    }

    private String getMessage(String oldName, String newName) {
        return messageSource.getMessage(
                "notification.category.name_update",
                new Object[] { oldName, newName },
                Locale.getDefault()
        );
    }

    private void sendCategoryUpdateNotification(String oldName, String newName) {
        Notification notification = new Notification(
                "Category update notification",
                getMessage(oldName, newName),
                NotificationType.INFO
        );
        notificationService.sendNotificationToProviders(notification);
    }

}
