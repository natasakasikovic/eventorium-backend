package com.iss.eventorium.event.provider;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.event.AgendaRequestDto;
import com.iss.eventorium.event.dtos.event.EventRequestDto;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.shared.dtos.CityDto;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

    public static Stream<EventRequestDto> provideValidEventRequest() {
        return Stream.of(
                EventRequestDto.builder()
                        .name("Test event")
                        .eventType(null)
                        .date(LocalDate.now().plusDays(3))
                        .city(new CityDto(1L, "Test city"))
                        .address("Test street")
                        .maxParticipants(20)
                        .description("Test description")
                        .privacy(Privacy.OPEN)
                        .build()
        );
    }

    public static Stream<Arguments> provideInvalidEventRequestsWithExpectedError() {
        return Stream.of(

                // name: blank
                Arguments.of(EventRequestDto.builder()
                        .name("  ")
                        .description("Valid")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.OPEN)
                        .maxParticipants(10)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Event name is required"),

                // name: too long
                Arguments.of(EventRequestDto.builder()
                        .name("a".repeat(51))
                        .description("Valid")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.OPEN)
                        .maxParticipants(10)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Event name must not exceed 50 characters"),

                // description: blank
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description(" ")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.CLOSED)
                        .maxParticipants(10)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Event description is required"),

                // description: too long
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("a".repeat(201))
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.CLOSED)
                        .maxParticipants(10)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Event description must not exceed 200 characters"),

                // date: null
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("Valid")
                        .date(null)
                        .privacy(Privacy.OPEN)
                        .maxParticipants(10)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Event date is required"),

                // date: in the past
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("Valid")
                        .date(LocalDate.now().minusDays(1))
                        .privacy(Privacy.OPEN)
                        .maxParticipants(10)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Event date must not be in the past"),

                // privacy: null
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("Valid")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(null)
                        .maxParticipants(10)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Privacy field is required"),

                // maxParticipants: null
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("Valid")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.CLOSED)
                        .maxParticipants(null)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Max participants field is required"),

                // maxParticipants: zero
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("Valid")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.CLOSED)
                        .maxParticipants(0)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Max participants must be a positive number greater than zero"),

                // maxParticipants: negative
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("Valid")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.CLOSED)
                        .maxParticipants(-1)
                        .city(new CityDto(1L, "City"))
                        .address("Address")
                        .build(), "Max participants must be a positive number greater than zero"),

                // city: null
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("Valid")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.CLOSED)
                        .maxParticipants(10)
                        .city(null)
                        .address("Address")
                        .build(), "City field is required"),

                // address: blank
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("Valid")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.CLOSED)
                        .maxParticipants(10)
                        .city(new CityDto(1L, "City"))
                        .address(" ")
                        .build(), "Address field is required"),

                // address: too long
                Arguments.of(EventRequestDto.builder()
                        .name("Valid")
                        .description("Valid")
                        .date(LocalDate.now().plusDays(1))
                        .privacy(Privacy.CLOSED)
                        .maxParticipants(10)
                        .city(new CityDto(1L, "City"))
                        .address("a".repeat(51))
                        .build(), "Address must not exceed 50 characters")
        );
    }

    public static Stream<Arguments> provideValidAgenda() {
        return Stream.of(
                Arguments.of(
                        AgendaRequestDto.builder().activities(List.of(
                            ActivityRequestDto.builder()
                                    .name("activity 1")
                                    .description("description")
                                    .startTime(LocalTime.of(10, 0))
                                    .endTime(LocalTime.of(11, 0))
                                    .location("location")
                                    .build(),
                            ActivityRequestDto.builder()
                                    .name("activity 2")
                                    .description("description")
                                    .startTime(LocalTime.of(11, 0))
                                    .endTime(LocalTime.of(12, 0))
                                    .location("location")
                                    .build())).build()
                        )
        );
    }


    public static Stream<Arguments> provideInvalidAgenda() {
        return Stream.of(
                // name is blank
                Arguments.of(
                        AgendaRequestDto.builder()
                                .activities(List.of(
                                        ActivityRequestDto.builder()
                                                .name("")
                                                .description("Valid description")
                                                .startTime(LocalTime.of(10, 0))
                                                .endTime(LocalTime.of(11, 0))
                                                .location("Valid location")
                                                .build()
                                ))
                                .build(),
                        "Activity name is required"
                ),

                // name too long
                Arguments.of(
                        AgendaRequestDto.builder()
                                .activities(List.of(
                                        ActivityRequestDto.builder()
                                                .name("a".repeat(51))
                                                .description("Valid description")
                                                .startTime(LocalTime.of(10, 0))
                                                .endTime(LocalTime.of(11, 0))
                                                .location("Valid location")
                                                .build()
                                ))
                                .build(),
                        "Activity name must not exceed 50 characters"
                ),

                // description is blank
                Arguments.of(
                        AgendaRequestDto.builder()
                                .activities(List.of(
                                        ActivityRequestDto.builder()
                                                .name("Valid name")
                                                .description("")
                                                .startTime(LocalTime.of(10, 0))
                                                .endTime(LocalTime.of(11, 0))
                                                .location("Valid location")
                                                .build()
                                ))
                                .build(),
                        "Activity description is required"
                ),

                // description too long
                Arguments.of(
                        AgendaRequestDto.builder()
                                .activities(List.of(
                                        ActivityRequestDto.builder()
                                                .name("Valid name")
                                                .description("a".repeat(201))
                                                .startTime(LocalTime.of(10, 0))
                                                .endTime(LocalTime.of(11, 0))
                                                .location("Valid location")
                                                .build()
                                ))
                                .build(),
                        "Event description must not exceed 200 characters"
                ),

                // startTime is null
                Arguments.of(
                        AgendaRequestDto.builder()
                                .activities(List.of(
                                        ActivityRequestDto.builder()
                                                .name("Valid name")
                                                .description("Valid description")
                                                .startTime(null)
                                                .endTime(LocalTime.of(11, 0))
                                                .location("Valid location")
                                                .build()
                                ))
                                .build(),
                        "Start time is required"
                ),

                // endTime is null
                Arguments.of(
                        AgendaRequestDto.builder()
                                .activities(List.of(
                                        ActivityRequestDto.builder()
                                                .name("Valid name")
                                                .description("Valid description")
                                                .startTime(LocalTime.of(10, 0))
                                                .endTime(null)
                                                .location("Valid location")
                                                .build()
                                ))
                                .build(),
                        "End time is required"
                ),
                // location is blank
                Arguments.of(
                        AgendaRequestDto.builder()
                                .activities(List.of(
                                        ActivityRequestDto.builder()
                                                .name("Valid name")
                                                .description("Valid description")
                                                .startTime(LocalTime.of(10, 0))
                                                .endTime(LocalTime.of(11, 0))
                                                .location("")
                                                .build()
                                ))
                                .build(),
                        "Location is required"
                ),

                // location too long
                Arguments.of(
                        AgendaRequestDto.builder()
                                .activities(List.of(
                                        ActivityRequestDto.builder()
                                                .name("Valid name")
                                                .description("Valid description")
                                                .startTime(LocalTime.of(10, 0))
                                                .endTime(LocalTime.of(11, 0))
                                                .location("a".repeat(51))
                                                .build()
                                ))
                                .build(),
                        "Location must not exceed 50 characters"
                )
        );
    }


}
