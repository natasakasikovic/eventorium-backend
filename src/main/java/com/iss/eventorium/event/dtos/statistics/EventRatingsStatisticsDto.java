package com.iss.eventorium.event.dtos.statistics;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRatingsStatisticsDto {
    private String eventName;
    private int totalVisitors;
    private int totalRatings;
    private Map<Integer, Integer> ratingsCount;
}
