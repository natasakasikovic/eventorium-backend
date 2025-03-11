package com.iss.eventorium.shared.mappers;

import com.iss.eventorium.shared.dtos.CityDto;
import com.iss.eventorium.shared.models.City;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityMapper {

    private final ModelMapper modelMapper;

    public City fromRequest(CityDto cityDto) {
        return modelMapper.map(cityDto, City.class);
    }

    public CityDto toResponse(City city) {
        return modelMapper.map(city, CityDto.class);
    }
}