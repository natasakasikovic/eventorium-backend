package com.iss.eventorium.user.repositories;

import com.iss.eventorium.user.models.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> { }