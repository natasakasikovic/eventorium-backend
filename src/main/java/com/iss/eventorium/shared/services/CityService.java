package com.iss.eventorium.shared.services;

import com.iss.eventorium.shared.dtos.CityDto;
import com.iss.eventorium.shared.mappers.CityMapper;
import com.iss.eventorium.shared.repositories.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    private final CityMapper mapper;

    public List<CityDto> getAll() {
        return cityRepository.findAll().stream()
                .map(mapper::toResponse).toList();
    }
}
