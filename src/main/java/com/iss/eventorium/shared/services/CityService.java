package com.iss.eventorium.shared.services;

import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.shared.repositories.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public List<City> getAll() {
        return cityRepository.findAll();
    }
}
