package com.iss.eventorium.product.dtos;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.eventtype.dtos.EventTypeResponseDto;
import com.iss.eventorium.shared.models.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    private String name;
    private String description;
    private String specialties;
    private Double price;
    private Double discount;
    private Status status;
    private List<EventTypeResponseDto> eventTypes;
    private CategoryRequestDto category;
}
