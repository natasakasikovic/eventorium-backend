package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.event.EventSummaryResponseDto;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountEventService {
    private final AuthService authService;
    private final EventService eventService;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public List<EventSummaryResponseDto> getFavouriteEvents() {
        return authService.getCurrentUser().getPerson().getFavouriteEvents()
                .stream().map(event -> eventMapper.toSummaryResponse(event)).toList();
    }

    public void addFavouriteEvent(Long id) {
        Event event = eventService.find(id);
        User user = authService.getCurrentUser();
        List<Event> favouriteEvents = user.getPerson().getFavouriteEvents();
        if (!favouriteEvents.contains(event)) {
            favouriteEvents.add(event);
            userRepository.save(user);
        }
    }

    public void removeFavouriteEvent(Long id) {
        Event event = eventService.find(id);
        User user = authService.getCurrentUser();
        List<Event> favouriteEvents = user.getPerson().getFavouriteEvents();
        favouriteEvents.remove(event);
        userRepository.save(user);
    }

    public boolean isFavouriteEvent(Long id) {
        Event event = eventService.find(id);
        return authService.getCurrentUser().getPerson().getFavouriteEvents().contains(event);
    }
}