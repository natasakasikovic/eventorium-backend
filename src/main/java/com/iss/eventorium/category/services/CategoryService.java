package com.iss.eventorium.category.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getCategories() {
        return categoryRepository.findByDeletedFalse();
    }

    public Page<Category> getCategoriesPaged(Pageable pageable) {
        return categoryRepository.findByDeletedFalse(pageable);
    }

    public Category getCategory(Long id) {
        return categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category toUpdate = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(EntityNotFoundException::new);
        toUpdate.setName(category.getName());
        toUpdate.setDescription(category.getDescription());
        return categoryRepository.save(toUpdate);
    }

    public void deleteCategory(Long id) {
        Category toDelete = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(EntityNotFoundException::new);
        // TODO: Check if it is possible to delete
        toDelete.setDeleted(true);
        categoryRepository.save(toDelete);
    }
}
