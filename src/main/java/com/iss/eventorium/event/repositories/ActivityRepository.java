package com.iss.eventorium.event.repositories;

import com.iss.eventorium.event.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
