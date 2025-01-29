package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.event.CalendarEventDto;
import com.iss.eventorium.event.dtos.event.EventSummaryResponseDto;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.user.models.Person;
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
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final UserRepository userRepository;

    public List<EventSummaryResponseDto> getFavouriteEvents() {
        return authService.getCurrentUser().getPerson().getFavouriteEvents()
                .stream().map(EventMapper::toSummaryResponse).toList();
    }

    public List<CalendarEventDto> getOrganizerEvents() {
        return eventRepository.findByOrganizer_Id(authService.getCurrentUser().getId()).stream()
                .map(EventMapper::toCalendarEvent)
                .toList();
    }

    public List<CalendarEventDto> getAttendingEvents() {
        Person person = authService.getCurrentUser().getPerson();
        return person.getAttendingEvents().stream().map(EventMapper::toCalendarEvent).toList();
    }

    public void markAttendance(Long eventId) {
        Event event = eventService.find(eventId);
        User user = authService.getCurrentUser();
        markAttendance(event, user);
    }

    public void markAttendance(Event event, User user) {
        if (!user.getPerson().getAttendingEvents().contains(event)) {
            addEventToUserAttendance(user, event);
        }
    }

    private void addEventToUserAttendance(User user, Event event) {
        user.getPerson().getAttendingEvents().add(event);
        userRepository.save(user);
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