package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {

    @Query("SELECT s FROM Service s JOIN s.reviews r WHERE s.status = 'ACCEPTED' AND r.status = 'ACCEPTED' GROUP BY s.id ORDER BY AVG(r.rating) DESC")
    List<Service> findTopFiveServices(Pageable pageable);
    Optional<Service> findByCategoryId(Long categoryId);

    @Query("SELECT s FROM Service s JOIN s.category c WHERE c.id = :categoryId AND s.price <= :price ORDER BY s.price DESC")
    List<Service> getBudgetSuggestions(Long categoryId, Double price);

    Page<Service> findByNameContainingAllIgnoreCase(String keyword, Pageable pageable);
    List<Service> findByNameContainingAllIgnoreCase(String keyword);
    List<Service> findByProvider_Id(Long id);
    Page<Service> findByProvider_Id(Long id, Pageable pageable);

    boolean existsByCategory_Id(Long categoryId);
}
