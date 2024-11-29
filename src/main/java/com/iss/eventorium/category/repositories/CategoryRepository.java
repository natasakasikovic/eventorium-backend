package com.iss.eventorium.category.repositories;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.shared.models.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDeletedFalse();
    Page<Category> findByDeletedFalse(Pageable pageable);
    Optional<Category> findByIdAndDeletedFalse(Long id);
    List<Category> findByStatus(Status status);
    Page<Category> findByStatus(Status status, Pageable pageable);
}
