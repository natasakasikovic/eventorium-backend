package com.iss.eventorium.eventtype.dtos;

import com.iss.eventorium.category.models.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTypeResponseDto {
    private Long id;
    private String name;
    private String description;
    private Category category;
}
