package com.iss.eventorium.event.dtos.eventtype;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTypeResponseDto {
    private Long id;
    private String name;
    private String description;
    private List<CategoryResponseDto> suggestedCategories;
}
