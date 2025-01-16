package com.iss.eventorium.category.services;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.exceptions.CategoryAlreadyExistsException;
import com.iss.eventorium.category.exceptions.CategoryInUseException;
import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.repositories.ProductRepository;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

import static com.iss.eventorium.category.mappers.CategoryMapper.*;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final ProductRepository productRepository;

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

        if(!Objects.equals(toUpdate.getName(), category.getName())
                && categoryRepository.findByName(category.getName()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + category.getName() + " already exists!");
        }

        toUpdate.setName(category.getName());
        toUpdate.setDescription(category.getDescription());
        return toResponse(categoryRepository.save(toUpdate));
    }

    public void deleteCategory(Long id) {
        Category toDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        if(serviceRepository.existsByCategory_Id(id)) {
            throw new CategoryInUseException("Unable to delete category because it is currently associated with an active service.");
        }
        if(productRepository.existsByCategory_Id(id)) {
            throw new CategoryInUseException("Unable to delete category because it is currently associated with an active product.");
        }
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
}
