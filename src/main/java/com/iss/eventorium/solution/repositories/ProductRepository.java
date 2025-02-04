package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p")
    List<Product> findTopFiveProducts(Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN p.category c " +
            "WHERE c.id = :categoryId AND p.price <= :price AND p.status = 'ACCEPTED' " +
            "ORDER BY p.price DESC")
    List<Product> getBudgetSuggestions(Long categoryId, Double price);
}