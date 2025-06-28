package com.iss.eventorium.user.repositories;

import com.iss.eventorium.user.models.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    @Query("SELECT e FROM UserReport e WHERE e.status = 'PENDING'")
    List<UserReport> findByStatusPending();
}