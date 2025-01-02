package com.iss.eventorium.event.dtos.eventtype;

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
public class EventTypeResponseDto {
    private Long id;
    private String name;
    private String description;
    private List<CategoryResponseDto> suggestedCategories;
}
