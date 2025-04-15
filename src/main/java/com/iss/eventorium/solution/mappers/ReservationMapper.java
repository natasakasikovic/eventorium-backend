package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.solution.dtos.services.CalendarReservationDto;
import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.solution.dtos.services.ReservationResponseDto;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.models.Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public ReservationMapper(ModelMapper modelMapper) {
        ReservationMapper.modelMapper = modelMapper;
    }

    public static Reservation fromRequest(ReservationRequestDto request, Event event, Service service) {
        Reservation reservation = modelMapper.map(request, Reservation.class);

        reservation.setEvent(event);
        reservation.setService(service);

        return reservation;
    }

    public static CalendarReservationDto toCalendarReservation(Reservation reservation) {
        return CalendarReservationDto.builder()
                .eventName(reservation.getEvent().getName())
                .serviceName(reservation.getService().getName())
                .date(reservation.getEvent().getDate())
                .build();
    }

    public static ReservationResponseDto toResponse(Reservation reservation) {
        return ReservationResponseDto.builder()
                .id(reservation.getId())
                .startingTime(reservation.getStartingTime())
                .endingTime(reservation.getEndingTime())
                .eventId(reservation.getEvent().getId())
                .eventName(reservation.getEvent().getName())
                .serviceId(reservation.getService().getId())
                .serviceName(reservation.getService().getName())
                .date(reservation.getEvent().getDate())
                .build();
    }
}