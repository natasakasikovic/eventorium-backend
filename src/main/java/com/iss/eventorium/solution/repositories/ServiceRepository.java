package com.iss.eventorium.solution.repositories;

import com.iss.eventorium.solution.models.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {

    @Query("SELECT s FROM Service s")
    List<Service> findTopFiveServices(Pageable pageable);

    @Query("SELECT s " +
            "FROM Service s " +
            "JOIN s.category c " +
            "WHERE c.id = :categoryId " +
            "AND s.price * (1 - s.discount / 100) <= :price " +
            "AND s.isAvailable = true " +
            "AND s.status = 'ACCEPTED'" +
            "ORDER BY s.price DESC")
    List<Service> getSuggestedServices(Long categoryId, Double price);

}
