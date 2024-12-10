package com.iss.eventorium.shared.repositories;

import com.iss.eventorium.shared.models.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
