package com.iss.eventorium.category.repositories;

import com.iss.eventorium.category.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findBySuggestedFalse();
    Page<Category> findBySuggestedFalse(Pageable pageable);
    Optional<Category> findByName(String name);
    List<Category> findBySuggestedTrue();
    Page<Category> findBySuggestedTrue(Pageable pageable);
}


