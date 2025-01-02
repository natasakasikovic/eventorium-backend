package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.BudgetItemRequestDto;
import com.iss.eventorium.event.models.BudgetItem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public BudgetMapper(ModelMapper modelMapper) {
        BudgetMapper.modelMapper = modelMapper;
    }


    public static BudgetItem fromRequest(BudgetItemRequestDto dto) {
        return modelMapper.map(dto, BudgetItem.class);
    }

}
