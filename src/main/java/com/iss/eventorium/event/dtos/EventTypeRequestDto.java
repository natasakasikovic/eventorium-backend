package com.iss.eventorium.event.dtos;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventTypeRequestDto {
    private String name;
    private String description;
    private List<CategoryResponseDto> suggestedCategories;
}
