package com.iss.eventorium.event.dtos.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSummaryResponseDto {
    private Long id;
    private Long imageId;
    private String name;
    private String city;
}
