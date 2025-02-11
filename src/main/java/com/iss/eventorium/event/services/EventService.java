package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.ActivityResponseDto;
import com.iss.eventorium.event.dtos.event.*;
import com.iss.eventorium.event.mappers.ActivityMapper;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.event.models.Activity;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.specifications.EventSpecification;
import com.iss.eventorium.shared.mappers.CityMapper;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.shared.services.PdfService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import com.iss.eventorium.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.time.LocalDate;


@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository repository;
    private final AuthService authService;
    private final PdfService pdfService;
    private final UserService userService;
    private final EventRepository eventRepository;

    public EditableEventDto getEvent(Long id) {
        return EventMapper.toEditableEvent(find(id));
    }

    public EventDetailsDto getEventDetails(Long id) {
        return EventMapper.toEventDetailsDto(find(id));
    }

    public List<EventSummaryResponseDto> getTopEvents() {
        Specification<Event> specification = EventSpecification.filterTopEvents(getUserCity(), authService.getCurrentUser());
        List<Event> events = repository.findAll(specification).stream()
                                        .sorted(Comparator.comparing(Event::getDate))
                                        .limit(5).toList();
        return events.stream().map(EventMapper::toSummaryResponse).toList();
    }

    private String getUserCity() {  // If the user is logged in, it returns the city from the profile, otherwise defaults to "Novi Sad".
        if (authService.getCurrentUser() != null)
            return authService.getCurrentUser().getPerson().getCity().getName();
        return "Novi Sad";
    }

    public List<EventSummaryResponseDto> getAll() {
        Specification<Event> specification = EventSpecification.filterByPrivacy(Privacy.OPEN, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(EventMapper::toSummaryResponse).toList();
    }

    public PagedResponse<EventSummaryResponseDto> searchEvents(String keyword, Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterByName(keyword, authService.getCurrentUser());
        return EventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<EventSummaryResponseDto> searchEvents(String keyword) {
        Specification<Event> specification = EventSpecification.filterByName(keyword, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(EventMapper::toSummaryResponse).toList();
    }

    public PagedResponse<EventSummaryResponseDto> getEventsPaged(Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterByPrivacy(Privacy.OPEN, authService.getCurrentUser());
        return EventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public PagedResponse<EventSummaryResponseDto> filterEvents(EventFilterDto filter, Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterBy(filter, authService.getCurrentUser());
        return EventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public Event find(Long id) {
        Specification<Event> specification = EventSpecification.filterById(id, authService.getCurrentUser());
        return repository.findOne(specification).orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    public EventResponseDto createEvent(EventRequestDto eventRequestDto)  {
        Event created = repository.save(prepareEvent(eventRequestDto));
        return EventMapper.toResponse(created);
    }

    private Event prepareEvent(EventRequestDto eventRequestDto) {
        Event event = EventMapper.fromRequest(eventRequestDto);
        event.setOrganizer(authService.getCurrentUser());
        return event;
    }

    public void setIsDraftFalse(Event event) {
        event.setDraft(false);
        repository.save(event);
    }

    public void updateEvent(Long id, UpdateEventRequestDto request) {
        Event event = find(id);
        if (!hasChanges(event, request)) return;
        // TODO: SEND EMAIL TO ATTENDEES INFORMING THEM ABOUT THE CHANGES
        // TODO: CANCEL RESERVATIONS IF THE EVENT DATE HAS BEEN CHANGED
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setDate(request.getDate());
        event.setMaxParticipants(request.getMaxParticipants());
        event.setType(EventTypeMapper.fromResponse(request.getEventType()));
        event.setCity(CityMapper.fromRequest(request.getCity()));
        event.setAddress(request.getAddress());
        eventRepository.save(event);
    }

    private boolean hasChanges(Event event, UpdateEventRequestDto request) {
        return !Objects.equals(event.getName(), request.getName()) ||
                !Objects.equals(event.getDescription(), request.getDescription()) ||
                !Objects.equals(event.getDate(), request.getDate()) ||
                !Objects.equals(event.getMaxParticipants(), request.getMaxParticipants()) ||
                !Objects.equals(event.getType().getId(), request.getEventType().getId()) ||
                !Objects.equals(event.getCity().getId(), request.getCity().getId()) ||
                !Objects.equals(event.getAddress(), request.getAddress());
    }

    public void createAgenda(Long id, List<ActivityRequestDto> request) {
        Event event = find(id);

        List<Activity> activities = request.stream()
                .map(ActivityMapper::fromRequest)
                .toList();

        event.getActivities().clear();
        event.getActivities().addAll(activities);

        if (event.getPrivacy().equals(Privacy.OPEN))
            setIsDraftFalse(event);
        else
            repository.save(event);
    }

    public List<ActivityResponseDto> getAgenda(Long id) {
        return find(id).getActivities().stream().map(ActivityMapper::toResponse).toList();
    }

    // TODO: this method needs to be replaces with method which will get my events in future
    public List<EventResponseDto> getDraftedEvents() {
        return repository.findByIsDraftTrueAndOrganizer_Id(authService.getCurrentUser().getId())
                .stream()
                .map(EventMapper::toResponse)
                .toList();
    }

    public byte[] generateEventDetailsPdf(Long id) {
        Event event = find(id);
        return pdfService.generate("/templates/event-details.jrxml", List.of(event), generateParams(event));
    }

    public byte[] generateGuestListPdf(Long id) {
        List<User> guests = userService.findByEventAttendance(id);
        return pdfService.generate("/templates/guest-list-pdf.jrxml", guests, generateParams(find(id)));
    }

    private Map<String, Object> generateParams(Event event) {
        Map<String, Object> params = new HashMap<>();
        params.put("eventName", event.getName());
        params.put("generatedDate", LocalDate.now());
        return params;
    }
}
