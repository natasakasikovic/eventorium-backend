package com.iss.eventorium.shared.mappers;

import com.iss.eventorium.shared.dtos.CityDto;
import com.iss.eventorium.shared.models.City;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public CityMapper(ModelMapper modelMapper) { CityMapper.modelMapper = modelMapper; }

    public static City fromRequest(CityDto cityDto) {
        return modelMapper.map(cityDto, City.class);
    }

    public static CityDto toResponse(City city) {
        return modelMapper.map(city, CityDto.class);
    }
}