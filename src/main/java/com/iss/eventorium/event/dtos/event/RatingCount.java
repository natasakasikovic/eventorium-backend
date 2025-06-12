package com.iss.eventorium.event.dtos.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingCount {
    private Integer rating;
    private Integer count;
}