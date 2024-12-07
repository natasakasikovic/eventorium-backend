package com.iss.eventorium.category.services;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import com.iss.eventorium.shared.utils.PagedResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.iss.eventorium.category.mappers.CategoryMapper.toPagedResponse;
import static com.iss.eventorium.category.mappers.CategoryMapper.toResponse;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

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

}
