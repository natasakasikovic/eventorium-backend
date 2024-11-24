package com.iss.eventorium.shared.models;

import com.iss.eventorium.category.models.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventType {
    private Long id;
    private String name;
    private String description;
    private Category category;
    private boolean isActive;
}
