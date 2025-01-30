package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.ActivityResponseDto;
import com.iss.eventorium.event.dtos.event.*;
import com.iss.eventorium.event.mappers.ActivityMapper;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.models.Activity;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.specifications.EventSpecification;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.shared.services.PdfService;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository repository;
    private final AuthService authService;
    private final PdfService pdfService;

    public EventDetailsDto getEventDetails(Long id) {
        Event event = find(id);
        return EventMapper.toEventDetailsDto(event);
    }

    public List<EventSummaryResponseDto> getTopEvents() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Event> events = repository.findTopFiveUpcomingEvents(getUserCity(), pageable);
        return events.stream().map(EventMapper::toSummaryResponse).collect(Collectors.toList());
    }

    private String getUserCity() {  // If the user is logged in, it returns the city from the profile, otherwise defaults to "Novi Sad".
        if (authService.getCurrentUser() != null)
            return authService.getCurrentUser().getPerson().getCity().getName();
        return "Novi Sad";
    }

    public List<EventSummaryResponseDto> getAll() {
        Specification<Event> specification = EventSpecification.filterByPrivacy(Privacy.OPEN);
        return repository.findAll(specification).stream().map(EventMapper::toSummaryResponse).toList();
    }

    public PagedResponse<EventSummaryResponseDto> searchEvents(String keyword, Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterByName(keyword);
        return EventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<EventSummaryResponseDto> searchEvents(String keyword) {
        Specification<Event> specification = EventSpecification.filterByName(keyword);
        return repository.findAll(specification).stream().map(EventMapper::toSummaryResponse).toList();
    }

    public PagedResponse<EventSummaryResponseDto> getEventsPaged(Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterByPrivacy(Privacy.OPEN);
        return EventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public PagedResponse<EventSummaryResponseDto> filterEvents(EventFilterDto filter, Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterBy(filter);
        return EventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public Event find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));
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

    public List<EventResponseDto> getDraftedEvents() {
        return repository.findByIsDraftTrueAndOrganizer_Id(authService.getCurrentUser().getId())
                .stream()
                .map(EventMapper::toResponse)
                .toList();
    }

    public byte[] generatePdf(Long id) throws JRException {
        Event event = find(id);
        Map<String, Object> params = new HashMap<>();
        params.put("reportTitle", "EVENT: " + event.getName());
        params.put("generatedDate", LocalDate.now());
        List<Event> data = List.of(event);
        return pdfService.generate("/templates/event-details.jrxml", data, params);
    }
}
