package com.iss.eventorium.category.services;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.iss.eventorium.category.mappers.CategoryMapper.*;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;

    public List<CategoryResponseDto> getCategories() {
        return categoryRepository.findBySuggestedFalse().stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    public PagedResponse<CategoryResponseDto> getCategoriesPaged(Pageable pageable) {
        return CategoryMapper
                .toPagedResponse(categoryRepository.findBySuggestedFalse(pageable));
    }

    public CategoryResponseDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        return toResponse(category);
    }

    public CategoryResponseDto createCategory(CategoryRequestDto category) {
        Category created = CategoryMapper.fromRequest(category);
        created.setSuggested(false);
        return toResponse(categoryRepository.save(created));
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto category) {
        Category toUpdate = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        toUpdate.setName(category.getName());
        toUpdate.setDescription(category.getDescription());
        return toResponse(categoryRepository.save(toUpdate));
    }

    public void deleteCategory(Long id) {
        Category toDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        // TODO: Check if it is possible to delete
        toDelete.setDeleted(true);
        categoryRepository.save(toDelete);
    }

    public List<CategoryResponseDto> getPendingCategories() {
        return categoryRepository.findBySuggestedTrue().stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    public PagedResponse<CategoryResponseDto> getPendingCategoriesPaged(Pageable pageable) {
        return toPagedResponse(categoryRepository.findBySuggestedTrue(pageable));
    }

    public CategoryResponseDto updateCategoryStatus(Long categoryId, Status status) {
        Category category = getCategoryProposal(categoryId);
        Service service = getServiceProposal(category);

        service.setStatus(status);
        if(status == Status.DECLINED) {
            category.setDeleted(true);
        } else {
            category.setSuggested(false);
        }
        categoryRepository.save(category);
        service.setCategory(category);
        serviceRepository.save(service);
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

        return toResponse(response);
    }

    public CategoryResponseDto changeCategoryProposal(Long categoryId, CategoryRequestDto dto) {
        Category category = getCategoryProposal(categoryId);
        Service service = getServiceProposal(category);

        service.setStatus(Status.ACCEPTED);
        service.setCategory(categoryRepository.findByName(dto.getName())
                .orElseThrow(() -> new EntityNotFoundException("Category with name " + dto.getName() + " not found")));

        categoryRepository.delete(category);
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
