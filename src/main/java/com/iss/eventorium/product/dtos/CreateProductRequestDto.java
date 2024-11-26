package com.iss.eventorium.product.dtos;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.eventtype.dtos.EventTypeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequestDto {
    private String name;
    private String description;
    private String specialties;
    private double price;
    private double discount;
    private List<EventTypeResponseDto> eventTypes;
    private CategoryResponseDto category;
}
