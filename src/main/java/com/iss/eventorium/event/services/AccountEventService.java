package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.event.CalendarEventDto;
import com.iss.eventorium.event.dtos.event.EventSummaryResponseDto;
import com.iss.eventorium.event.exceptions.EventAlreadyPassedException;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.specifications.EventSpecification;
import com.iss.eventorium.interaction.services.RatingService;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountEventService {

    private final EventRepository repository;
    private final AuthService authService;
    private final EventService eventService;
    private final UserRepository userRepository;

    private final EventMapper mapper;

    public List<EventSummaryResponseDto> getFavouriteEvents() {
        return authService.getCurrentUser().getPerson().getFavouriteEvents()
                .stream().map(mapper::toSummaryResponse).toList();
    }

    public List<CalendarEventDto> getOrganizerEvents() {
        return findOrganizerEvents(authService.getCurrentUser()).stream().map(mapper::toCalendarEvent).toList();
    }

    public List<CalendarEventDto> getAttendingEvents() {
        Person person = authService.getCurrentUser().getPerson();
        return person.getAttendingEvents().stream().map(mapper::toCalendarEvent).toList();
    }

    public void markAttendance(Long eventId) {
        Event event = eventService.find(eventId);
        if (event.getDate().isBefore(LocalDate.now())) throw new EventAlreadyPassedException("Event has already passed.");
        User user = authService.getCurrentUser();
        markAttendance(event, user);
    }

    public void markAttendance(Event event, User user) {
        if (!user.getPerson().getAttendingEvents().contains(event))
            addEventToUserAttendance(user, event);
    }

    private void addEventToUserAttendance(User user, Event event) {
        user.getPerson().getAttendingEvents().add(event);
        userRepository.save(user);
    }

    public boolean isUserEligibleToRate(Long eventId) {
        // NOTE: A user can rate the event only if they marked attendance, the event has already ended, and they haven't already rated it.
        Event event = eventService.find(eventId);
        if (event.getDate().isAfter(LocalDate.now())) return false;
        User user = authService.getCurrentUser();
        return hasUserAttendedEvent(user, event) && !isRatedByUser(event, user);
    }

    private boolean hasUserAttendedEvent(User user, Event event) {
        return user.getPerson().getAttendingEvents().contains(event);
    }

    public boolean isRatedByUser(Event event, User user) {
        return event.getRatings().stream()
                .anyMatch(rating -> rating.getRater().equals(user));
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

    public List<EventSummaryResponseDto> getAll() {
        return findOrganizerEvents(authService.getCurrentUser()).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<EventSummaryResponseDto> getEventsPaged(Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterByOrganizer(authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<EventSummaryResponseDto> searchEvents(String keyword) {
        Specification<Event> specification = EventSpecification.filterByNameForOrganizer(keyword, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<EventSummaryResponseDto> searchEvents(String keyword, Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterByNameForOrganizer(keyword, authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<Event> findOrganizerEvents(User organizer) {
        Specification<Event> specification = EventSpecification.filterByOrganizer(organizer);
        return repository.findAll(specification);
    }
}