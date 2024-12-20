package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN p.reviews r WHERE p.status = 'ACCEPTED' GROUP BY p.id ORDER BY AVG(r.rating) DESC")
    List<Product> findTopFiveProducts(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.id = :categoryId AND p.price <= :price ORDER BY p.price DESC")
    List<Product> getBudgetSuggestions(Long categoryId, Double price);

    Page<Product> findByNameContainingAllIgnoreCase(String keyword, Pageable pageable);
}