package com.iss.eventorium.category.repositories;

import com.iss.eventorium.category.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDeletedFalse();
    Page<Category> findByDeletedFalse(Pageable pageable);
    Optional<Category> findByIdAndDeletedFalse(Long id);
}
