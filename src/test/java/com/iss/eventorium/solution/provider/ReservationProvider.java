package com.iss.eventorium.solution.provider;

import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalTime;
import java.util.stream.Stream;

public class ReservationProvider {

    // NOTE: Provides reservations with valid durations in order to test reservations for a service whose duration is between 2 and 6 hours.
    private static Stream<Arguments> provideReservationsWithValidDurations() {
        return Stream.of(
                // Exactly 2 hours (minimum valid duration)
                Arguments.of(ReservationRequestDto.builder()
                        .startingTime(LocalTime.of(7, 0))
                        .endingTime(LocalTime.of(9, 0))
                        .plannedAmount(120.0)
                        .build()),
                // Exactly 4 hours (within range)
                Arguments.of(ReservationRequestDto.builder()
                        .startingTime(LocalTime.of(9, 0))
                        .endingTime(LocalTime.of(13, 0))
                        .plannedAmount(120.0)
                        .build()),
                // Exactly 6 hours (maximum valid duration)
                Arguments.of(ReservationRequestDto.builder()
                        .startingTime(LocalTime.of(14, 0))
                        .endingTime(LocalTime.of(20, 0))
                        .plannedAmount(120.0)
                        .build())
        );
    }

    // NOTE: Provides reservations with invalid durations in order to test reservations for a service whose duration is between 2 and 6 hours.
    private static Stream<Arguments> provideReservationsWithInvalidDurations() {
        return Stream.of(
                // One minute shorter than the minimum allowed duration (duration is 1h 59min)
                Arguments.of(ReservationRequestDto.builder()
                                .startingTime(LocalTime.of(10, 0))
                                .endingTime(LocalTime.of(11, 59))
                                .plannedAmount(100.0)
                                .build()),
                // One minute longer than the maximum allowed duration (duration is 6h 1min)
                Arguments.of(ReservationRequestDto.builder()
                        .startingTime(LocalTime.of(9, 0))
                        .endingTime(LocalTime.of(15, 1))
                        .plannedAmount(100.0)
                        .build()),
                // Duration of exactly zero hours (start and end time are the same)
                Arguments.of(ReservationRequestDto.builder()
                        .startingTime(LocalTime.of(12, 0))
                        .endingTime(LocalTime.of(12, 0))
                        .plannedAmount(100.0)
                        .build()),
                // Negative duration
                Arguments.of(ReservationRequestDto.builder()
                        .startingTime(LocalTime.of(14, 0))
                        .endingTime(LocalTime.of(13, 0))
                        .plannedAmount(100.0)
                        .build())
        );
    }

    private static Stream<Arguments> provideReservationsWithinCompanyWorkingHours() {
        return Stream.of(
                // // The reservation time is fully within the company working hours
                Arguments.of(ReservationRequestDto.builder()
                                .startingTime(LocalTime.of(10, 0))
                                .endingTime(LocalTime.of(12, 0))
                                .plannedAmount(150.0)
                                .build()),
                // Reservation starts exactly at company's opening time, ends before closing
                Arguments.of(ReservationRequestDto.builder()
                                .startingTime(LocalTime.of(8, 0))
                                .endingTime(LocalTime.of(10, 0))
                                .plannedAmount(150.0)
                                .build()),
                // Reservation starts after opening, ends exactly at closing time
                Arguments.of(ReservationRequestDto.builder()
                                .startingTime(LocalTime.of(12, 0))
                                .endingTime(LocalTime.of(14, 0))
                                .plannedAmount(150.0)
                                .build())
                );
    }

    private static Stream<Arguments> provideReservationsOutsideCompanyWorkingHours() {
        return Stream.of(
                // The reservation lasts one minute beyond the company's working hours.
                Arguments.of(ReservationRequestDto.builder()
                        .startingTime(LocalTime.of(10, 0))
                        .endingTime(LocalTime.of(14, 1))
                        .plannedAmount(150.0)
                        .build()),
                // The reservation starts one minute before the company’s opening time.
                Arguments.of(ReservationRequestDto.builder()
                         .startingTime(LocalTime.of(7, 59))
                         .endingTime(LocalTime.of(10, 0))
                         .plannedAmount(150.0)
                         .build()),
                // The reservation is completely outside the company’s working hours.
                Arguments.of(ReservationRequestDto.builder()
                        .startingTime(LocalTime.of(18, 0))
                        .endingTime(LocalTime.of(20, 0))
                        .plannedAmount(150.0)
                        .build())
        );
    }

    // NOTE: Supplies with invalid reservation requests and expected messages
    public static Stream<Arguments> provideInvalidReservationRequests() {
        return Stream.of(
                // starting time is null
                Arguments.of(ReservationRequestDto.builder()
                                .startingTime(null)
                                .endingTime(LocalTime.of(12, 0))
                                .plannedAmount(150.0)
                                .build(),
                        "Starting time is required!"),
                // ending time is null
                Arguments.of(ReservationRequestDto.builder()
                                .startingTime(LocalTime.of(9, 0))
                                .endingTime(null)
                                .plannedAmount(150.0)
                                .build(),
                        "Ending time is required!"),
                // planned amount is negative
                Arguments.of(ReservationRequestDto.builder()
                                .startingTime(LocalTime.of(9, 0))
                                .endingTime(LocalTime.of(12, 0))
                                .plannedAmount(-10.0)
                                .build(),
                        "Planned amount must be greater than zero"),
                // planned amount is zero
                Arguments.of(ReservationRequestDto.builder()
                                .startingTime(LocalTime.of(9, 0))
                                .endingTime(LocalTime.of(12, 0))
                                .plannedAmount(0.0)
                                .build(),
                        "Planned amount must be greater than zero")
        );
    }

    public static ReservationRequestDto provideReservationToCauseOverlapping() {
        return ReservationRequestDto.builder()
                .startingTime(LocalTime.of(11, 59))
                .endingTime(LocalTime.of(14, 0))
                .plannedAmount(150.0)
                .build();
    }

    public static ReservationRequestDto provideValidReservationForFixedServiceDurationTest() {
        return ReservationRequestDto.builder()
                .startingTime(LocalTime.of(8, 0))
                .endingTime(LocalTime.of(13, 0))
                .plannedAmount(1000.0)
                .build();
    }

    // NOTE: Supplies service price & discount pairs to test a reservation with a planned amount of 100.0
    private static Stream<Arguments> providePricesAndDiscountsThatFitPlannedAmount () {
        return Stream.of(
                Arguments.of(100.0, 0.0), // exact price match: 100.0 with 0% discount
                Arguments.of(200.0, 50.0), // boundary discount: 50% off 200 → 100.0
                Arguments.of(90.0, 0.0), // price under planned amount, no discount
                Arguments.of(105.0, 10.0) // price 105(higer than planned) but with 10% off → 94.5 (<100)
        );
    }

    // NOTE: Supplies service price & discount pairs to test cases where final price exceeds planned amount of 100.0
    private static Stream<Arguments> providePricesAndDiscountsThatDoNotFitPlannedAmount() {
        return Stream.of(
                Arguments.of(100.01, 0.0),      // just above planned amount, no discount
                Arguments.of(150.0, 5.0),       // significantly above planned amount, small discount insufficient
                Arguments.of(100.01, 0.005)     // minimal discount, but final price still slightly above 100.0 (100.01 - 0.005 = 100.005)
        );
    }
}