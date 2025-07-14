package com.iss.eventorium.solution.provider;

import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalTime;
import java.util.stream.Stream;

public class ReservationProvider {

    // NOTE: Provides reservations with valid durations in order to test reservations for a service whose duration is between 2 and 6 hours.
    private static Stream<Arguments> provideReservationsWithValidDurations() {
        return Stream.of(
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(12, 0)), // Exactly 2 hours (minimum valid duration)
                Arguments.of(LocalTime.of(13, 0), LocalTime.of(17, 0)), // Exactly 4 hours (within range)
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(16, 0)) // Exactly 6 hours (maximum valid duration)
        );
    }

    // NOTE: Provides reservations with invalid durations in order to test reservations for a service whose duration is between 2 and 6 hours.
    private static Stream<Arguments> provideReservationsWithInvalidDurations() {
        return Stream.of(
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(11, 59)), // One minute shorter than the minimum allowed duration (duration is 1h 59min)
                Arguments.of(LocalTime.of(9, 0), LocalTime.of(15, 1)),   // one minute longer than the maximum allowed duration (duration is 6h 1min)
                Arguments.of(LocalTime.of(12, 0), LocalTime.of(12, 0)),  // Duration of exactly zero hours (start and end time are the same)
                Arguments.of(LocalTime.of(14, 0), LocalTime.of(13, 0))   // Negative duration
        );
    }

    // NOTE: Provides valid company working hours for testing reservations that occur between 11:00 and 15:00.
    private static Stream<Arguments> provideValidWorkingHoursForCompany () {
        return Stream.of(
                Arguments.of(LocalTime.of(11, 0), LocalTime.of(15, 0)), // Company working hours exactly match the reservation (11:00 - 15:00)
                Arguments.of(LocalTime.of(8, 0), LocalTime.of(17, 0)), // Company working hours fully include the reservation (8:00 - 17:00)
                Arguments.of(LocalTime.of(11, 0), LocalTime.of(16, 0)), // Reservation starts exactly at opening time, ends before closing (11:00 - 16:00)
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(15, 0)) // Reservation starts after opening, ends exactly at closing time (10:00 - 15:00)
        );
    }

    // NOTE: Supplies company hours that will reject a reservation from 11:00 to 15:00.
    private static Stream<Arguments> provideInvalidWorkingHoursForCompany () {
        return Stream.of(
                Arguments.of(LocalTime.of(7, 0), LocalTime.of(14, 59)), // Company closes one minute before reservation ends (closing at 14:59, reservation ends at 15:00)
                Arguments.of(LocalTime.of(11, 1), LocalTime.of(16, 0)), // Company opens one minute after reservation starts (opening at 11:01, reservation starts at 11:00)
                Arguments.of(LocalTime.of(6, 0), LocalTime.of(10, 30))  // Company working hours completely outside the reservation request (company 6:00 - 10:30, reservation 11:00 - 15:00)
        );
    }
}