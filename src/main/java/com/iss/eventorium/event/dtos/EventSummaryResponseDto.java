package com.iss.eventorium.event.dtos;

import com.iss.eventorium.shared.models.Location;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSummaryResponseDto {
    private Long id;
    private String name;
    private String city;
}
