package com.iss.eventorium.category.models;

import com.iss.eventorium.shared.models.Status;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    private String name;
    private String description;
    private Status status;
    private boolean activated;
    private boolean deleted;
}
