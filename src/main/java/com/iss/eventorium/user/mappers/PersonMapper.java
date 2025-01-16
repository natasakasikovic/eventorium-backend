package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.update.UpdateRequestDto;
import com.iss.eventorium.user.models.Person;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {
    private static ModelMapper modelMapper;

    public PersonMapper(ModelMapper modelMapper) {
        PersonMapper.modelMapper = modelMapper;
    }

    public Person fromRequest(UpdateRequestDto personRequestDto) {
        return modelMapper.map(personRequestDto, Person.class);
    }
}
