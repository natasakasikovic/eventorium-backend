package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.event.EventSummaryResponseDto;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountEventService {

    private final AuthService authService;
    private final EventRepository eventRepository;

    public List<EventSummaryResponseDto> getEvents() {
        return eventRepository.findByOrganizer_Id(authService.getCurrentUser().getId()).stream()
                .map(EventMapper::toSummaryResponse)
                .toList();
    }
}
