package com.iss.eventorium.event.provider;

import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalTime;
import java.util.stream.Stream;

public class EventProvider {

    public static Stream<Arguments> provideInvalidTimeRanges() {
        return Stream.of(
                Arguments.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(9, 0),
                        "Invalid time range for activity 'Activity1': end time (09:00) must be after start time (10:00)."
                ),
                Arguments.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(10, 0),
                        "Invalid time range for activity 'Activity1': end time (10:00) must be after start time (10:00)."
                )
        );
    }
}
