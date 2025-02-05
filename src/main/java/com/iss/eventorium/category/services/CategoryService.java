package com.iss.eventorium.category.services;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.exceptions.CategoryAlreadyExistsException;
import com.iss.eventorium.category.exceptions.CategoryInUseException;
import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.models.Solution;
import com.iss.eventorium.solution.repositories.SolutionRepository;
import com.iss.eventorium.solution.services.SolutionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.iss.eventorium.category.mappers.CategoryMapper.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final SolutionService solutionService;

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
        return toResponse(find(id));
    }

    public CategoryResponseDto createCategory(CategoryRequestDto category) {
        if(categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new CategoryAlreadyExistsException("Category with name " + category.getName() + " already exists");
        }
        Category created = CategoryMapper.fromRequest(category);
        created.setSuggested(false);
        return toResponse(categoryRepository.save(created));
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto category) {
        Category toUpdate = find(id);

        if(checkCategoryExistence(toUpdate, category.getName())) {
            throw new CategoryAlreadyExistsException("Category with name " + category.getName() + " already exists!");
        }

        toUpdate.setName(category.getName());
        toUpdate.setDescription(category.getDescription());
        return toResponse(categoryRepository.save(toUpdate));
    }

    public void deleteCategory(Long id) {
        Category toDelete = find(id);

        if(solutionService.existsCategory(id)) {
            throw new CategoryInUseException("Unable to delete category because it is currently associated with an active solution.");
        }
        toDelete.setName(Instant.now().toEpochMilli() + "_" + toDelete.getName());
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

    public void ensureCategoryNameIsUnique(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new CategoryAlreadyExistsException("Category with name " + category.getName() + " already exists");
        }
    }

    public Category find(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public Category findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException("Category with name " + name + " not found"));
    }

    public boolean checkCategoryExistence(Category category, String name) {
        return !Objects.equals(category.getName(), name)
                && categoryRepository.existsByNameIgnoreCase(name);
    }

}
