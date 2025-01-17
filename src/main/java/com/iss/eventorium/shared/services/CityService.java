package com.iss.eventorium.shared.services;

import com.iss.eventorium.shared.dtos.CityDto;
import com.iss.eventorium.shared.mappers.CityMapper;
import com.iss.eventorium.shared.repositories.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public List<CityDto> getAll() {
        return cityRepository.findAll().stream()
                .map(CityMapper::toResponse).collect(Collectors.toList());
    }
}
