package com.iss.eventorium.category.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import com.iss.eventorium.shared.models.Status;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getCategories() {
        return categoryRepository.findByStatusAndDeletedFalse(Status.ACCEPTED);
    }

    public Page<Category> getCategoriesPaged(Pageable pageable) {
        return categoryRepository.findByStatusAndDeletedFalse(pageable, Status.ACCEPTED);
    }

    public Category getCategory(Long id) {
        return categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
    }

    public Category createCategory(Category category) {
        category.setStatus(Status.ACCEPTED);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category toUpdate = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        toUpdate.setName(category.getName());
        toUpdate.setDescription(category.getDescription());
        return categoryRepository.save(toUpdate);
    }

    public void deleteCategory(Long id) {
        Category toDelete = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        // TODO: Check if it is possible to delete
        toDelete.setDeleted(true);
        categoryRepository.save(toDelete);
    }

    public List<Category> getPendingCategories() {
        return categoryRepository.findByStatusAndDeletedFalse(Status.PENDING);
    }

    public Page<Category> getPendingCategoriesPaged(Pageable pageable) {
        return categoryRepository.findByStatusAndDeletedFalse(pageable, Status.PENDING);
    }
}
