package com.iss.eventorium.interaction.models;

import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.user.models.Person;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    private Long id;
    private LocalDateTime creationDate;
    private Integer rating;
    private String feedback;
    private Status status;
    private Person person;
}
