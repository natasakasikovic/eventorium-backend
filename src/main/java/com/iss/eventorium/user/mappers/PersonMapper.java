package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.user.UpdateRequestDto;
import com.iss.eventorium.user.models.Person;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonMapper {

    private final ModelMapper modelMapper;

    public Person fromRequest(UpdateRequestDto personRequestDto) {
        return modelMapper.map(personRequestDto, Person.class);
    }
}
