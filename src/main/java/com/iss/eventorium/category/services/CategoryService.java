package com.iss.eventorium.category.services;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.exceptions.CategoryAlreadyExistsException;
import com.iss.eventorium.category.exceptions.CategoryInUseException;
import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.services.SolutionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final SolutionService solutionService;

    private final CategoryRepository categoryRepository;

    private final CategoryMapper mapper;

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
        if(categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new CategoryAlreadyExistsException(category.getName());
        }
        Category created = mapper.fromRequest(category);
        created.setSuggested(false);
        return mapper.toResponse(categoryRepository.save(created));
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto category) {
        Category toUpdate = find(id);

        if(checkCategoryExistence(toUpdate, category.getName())) {
            throw new CategoryAlreadyExistsException(String.format(CATEGORY_ALREADY_EXISTS_MESSAGE, category.getName()));
        }

        toUpdate.setName(category.getName());
        toUpdate.setDescription(category.getDescription());
        return mapper.toResponse(categoryRepository.save(toUpdate));
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
                .map(mapper::toResponse)
                .toList();
    }

    public PagedResponse<CategoryResponseDto> getPendingCategoriesPaged(Pageable pageable) {
        return mapper.toPagedResponse(categoryRepository.findBySuggestedTrue(pageable));
    }

    public void ensureCategoryNameIsUnique(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new CategoryAlreadyExistsException(String.format(CATEGORY_ALREADY_EXISTS_MESSAGE, category.getName()));
        }
    }

    public Category find(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public Category findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(String.format(CATEGORY_ALREADY_EXISTS_MESSAGE, name)));
    }

    public boolean checkCategoryExistence(Category category, String name) {
        return !Objects.equals(category.getName(), name)
                && categoryRepository.existsByNameIgnoreCase(name);
    }

}
