package com.iss.eventorium.event.services;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.ActivityResponseDto;
import com.iss.eventorium.event.dtos.event.*;
import com.iss.eventorium.event.dtos.statistics.EventRatingsStatisticsDto;
import com.iss.eventorium.event.events.EventDateChangedEvent;
import com.iss.eventorium.event.exceptions.AgendaAlreadyDefinedException;
import com.iss.eventorium.event.exceptions.EmptyAgendaException;
import com.iss.eventorium.event.exceptions.InvalidEventStateException;
import com.iss.eventorium.event.mappers.ActivityMapper;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.event.models.Activity;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.specifications.EventSpecification;
import com.iss.eventorium.interaction.models.Rating;
import com.iss.eventorium.shared.exceptions.InvalidTimeRangeException;
import com.iss.eventorium.shared.exceptions.OwnershipRequiredException;
import com.iss.eventorium.shared.mappers.CityMapper;
import com.iss.eventorium.shared.models.EmailDetails;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.shared.services.EmailService;
import com.iss.eventorium.shared.services.PdfService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import com.iss.eventorium.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@RequiredArgsConstructor
@Service
@Slf4j
public class EventService {

    private final EventRepository repository;
    private final AuthService authService;
    private final PdfService pdfService;
    private final UserService userService;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    private final ActivityMapper activityMapper;
    private final EventMapper eventMapper;
    private final EventTypeMapper eventTypeMapper;

    public static final String EMAIL_SUBJECT = "Notification from Eventorium";
    public static final String EVENT_UPDATE_NOTIFICATION_TEMPLATE = "event-update-notification";
    private final SpringTemplateEngine templateEngine;
    private final CityMapper cityMapper;

    public EditableEventDto getEvent(Long id) {
        return eventMapper.toEditableEvent(find(id));
    }

    public EventDetailsDto getEventDetails(Long id) {
        return eventMapper.toEventDetailsDto(find(id));
    }

    public List<EventSummaryResponseDto> getTopEvents() {
        Specification<Event> specification = EventSpecification.filterTopEvents(getUserCity(), authService.getCurrentUser());
        List<Event> events = repository.findAll(specification).stream()
                                        .sorted(Comparator.comparing(Event::getDate))
                                        .limit(5).toList();
        return events.stream().map(eventMapper::toSummaryResponse).toList();
    }

    private String getUserCity() {  // If the user is logged in, it returns the city from the profile, otherwise defaults to "Novi Sad".
        if (authService.getCurrentUser() != null)
            return authService.getCurrentUser().getPerson().getCity().getName();
        return "Novi Sad";
    }

    public List<EventSummaryResponseDto> getAll() {
        Specification<Event> specification = EventSpecification.filterByPrivacy(Privacy.OPEN, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(eventMapper::toSummaryResponse).toList();
    }

    public List<EventTableOverviewDto> getPassedEvents() {
        Specification<Event> specification;
        User user = authService.getCurrentUser();
        if (user.getRoles().stream().anyMatch(role -> "EVENT_ORGANIZER".equals(role.getName())))
            specification = EventSpecification.filterPassedEventsByOrganizer(user);
        else
            specification = EventSpecification.filterPassedEvents();
        return repository.findAll(specification).stream().map(eventMapper::toTableOverviewDto).toList();
    }

    public PagedResponse<EventSummaryResponseDto> searchEventsPaged(String keyword, Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterByName(keyword, authService.getCurrentUser());
        return eventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<EventSummaryResponseDto> searchEvents(String keyword) {
        Specification<Event> specification = EventSpecification.filterByName(keyword, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(eventMapper::toSummaryResponse).toList();
    }

    public PagedResponse<EventSummaryResponseDto> getEventsPaged(Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterByPrivacy(Privacy.OPEN, authService.getCurrentUser());
        return eventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<EventSummaryResponseDto> filterEvents(EventFilterDto filter) {
        Specification<Event> specification = EventSpecification.filterBy(filter, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(eventMapper::toSummaryResponse).toList();
    }

    public PagedResponse<EventSummaryResponseDto> filterEventsPaged(EventFilterDto filter, Pageable pageable) {
        Specification<Event> specification = EventSpecification.filterBy(filter, authService.getCurrentUser());
        return eventMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public Event find(Long id) {
        Specification<Event> specification = EventSpecification.filterById(id, authService.getCurrentUser());
        return repository.findOne(specification).orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    public EventResponseDto createEvent(EventRequestDto eventRequestDto) {
        Event created = repository.save(prepareEvent(eventRequestDto));
        return eventMapper.toResponse(created);
    }

    private Event prepareEvent(EventRequestDto eventRequestDto) {
        Event event = eventMapper.fromRequest(eventRequestDto);
        event.setOrganizer(authService.getCurrentUser());
        return event;
    }

    public void setIsDraftFalse(Event event) {
        event.setDraft(false);
        repository.save(event);
    }

    public void updateEvent(Long id, UpdateEventRequestDto request) {
        Event event = find(id);
        assertOwnership(event);

        if (!hasChanges(event, request)) return;
        if (!Objects.equals(event.getDate(), request.getDate()))
            eventPublisher.publishEvent(new EventDateChangedEvent(this, id));

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setDate(request.getDate());
        event.setMaxParticipants(request.getMaxParticipants());
        if (request.getEventType() == null) event.setType(null);
        else event.setType(eventTypeMapper.fromResponse(request.getEventType()));
        event.setCity(cityMapper.fromRequest(request.getCity()));
        event.setAddress(request.getAddress());

        repository.save(event);
        notifyGuestsAboutChanges(event);
    }

    private boolean hasChanges(Event event, UpdateEventRequestDto request) {
        return !Objects.equals(event.getName(), request.getName()) ||
                !Objects.equals(event.getDescription(), request.getDescription()) ||
                !Objects.equals(event.getDate(), request.getDate()) ||
                !Objects.equals(event.getMaxParticipants(), request.getMaxParticipants()) ||
                !Objects.equals(
                        event.getType() != null ? event.getType().getId() : null,
                        request.getEventType() != null ? request.getEventType().getId() : null
                ) ||
                !Objects.equals(
                        event.getCity() != null ? event.getCity().getId() : null,
                        request.getCity() != null ? request.getCity().getId() : null
                ) ||
                !Objects.equals(event.getAddress(), request.getAddress());
    }


    private void notifyGuestsAboutChanges(Event event) {
        List<User> guests = userService.findByEventAttendance(event.getId());
        for (User guest: guests) {
            EmailDetails emailDetails = createEmailDetails(event, guest);
            emailService.sendSimpleMail(emailDetails);
        }
    }

    private EmailDetails createEmailDetails(Event event, User recipient) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(recipient.getEmail());
        emailDetails.setSubject(EMAIL_SUBJECT);
        emailDetails.setMsgBody(generateEmailContent(event));
        return emailDetails;
    }

    public String generateEmailContent(Event event) {
        Context context = new Context();
        context.setVariables(getContextVariables(event));
        return templateEngine.process(EVENT_UPDATE_NOTIFICATION_TEMPLATE, context);
    }

    private Map<String, Object> getContextVariables(Event event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("eventName", event.getName());
        variables.put("description", event.getDescription());
        variables.put("eventDate", event.getDate());
        variables.put("address", event.getAddress());
        variables.put("city", event.getCity().getName());
        return variables;
    }

    public void createAgenda(Long id, AgendaRequestDto agenda) {
        List<ActivityRequestDto> request = agenda.getActivities();
        if (request.isEmpty())
            throw new EmptyAgendaException("Agenda must contain at least one activity.");

        Event event = find(id);
        assertOwnership(event);
        if (!event.isDraft())
            throw new InvalidEventStateException("Cannot add agenda to event with name " + event.getName());
        if (!event.getActivities().isEmpty())
            throw new AgendaAlreadyDefinedException("Agenda already defined for event with name " + event.getName());

        List<Activity> activities = request.stream()
                .map(activityMapper::fromRequest)
                .toList();
        validateActivities(activities);

        event.setActivities(new ArrayList<>());
        event.getActivities().addAll(activities);

        if (event.getPrivacy().equals(Privacy.OPEN)) setIsDraftFalse(event);
        else repository.save(event);
    }

    private void validateActivities(List<Activity> activities) {
        for (Activity a : activities) {
            if (!a.getEndTime().isAfter(a.getStartTime())) {
                String message = String.format(
                        "Invalid time range for activity '%s': end time (%s) must be after start time (%s).",
                        a.getName(),
                        a.getEndTime(),
                        a.getStartTime()
                );
                throw new InvalidTimeRangeException(message);
            }
        }
    }

    public List<ActivityResponseDto> getAgenda(Long id) {
        return find(id).getActivities().stream().map(activityMapper::toResponse).toList();
    }

    public List<EventResponseDto> getFutureEvents() {
        Specification<Event> specification = EventSpecification.filterFutureEvents(authService.getCurrentUser());
        return repository.findAll(specification)
                .stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    public byte[] generateEventDetailsPdf(Long id) {
        Event event = find(id);
        return pdfService.generate("/templates/event-details.jrxml", List.of(event), generateParams(event));
    }

    public byte[] generateGuestListPdf(Long id) {
        assertOwnership(find(id));
        List<User> guests = userService.findByEventAttendance(id);
        return pdfService.generate("/templates/guest-list-pdf.jrxml", guests, generateParams(find(id)));
    }

    public byte[] generateEventStatisticsPdf(Long id) {
        EventRatingsStatisticsDto statistics = getEventRatingStatistics(id);
        List<RatingCount> chartData = statistics.getRatingsCount().entrySet().stream()
                .map(entry -> new RatingCount(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        Map<String, Object> params = generateParams(find(id));
        params.put("totalRatings", statistics.getTotalRatings());
        params.put("totalVisitors", statistics.getTotalVisitors());

        return pdfService.generate("/templates/event-stats.jrxml", chartData, params);
    }

    private Map<String, Object> generateParams(Event event) {
        Map<String, Object> params = new HashMap<>();
        params.put("eventName", event.getName());
        params.put("generatedDate", LocalDate.now());
        return params;
    }

    public void addRating(Event event, Rating rating) {
        event.getRatings().add(rating);
        repository.save(event);
    }

    public EventRatingsStatisticsDto getEventRatingStatistics(Long id) {
        Event event = find(id);
        int totalVisitors = userService.findByEventAttendance(event.getId()).size();
        int totalRatings = event.getRatings().size();

        Map<Integer, Integer> ratingsCount = IntStream.rangeClosed(1, 5)
                .boxed()
                .collect(Collectors.toMap(r -> r, r -> 0));

        event.getRatings().forEach(r ->
                ratingsCount.merge(r.getRating(), 1, Integer::sum)
        );

        return EventRatingsStatisticsDto.builder()
                .eventName(event.getName())
                .totalVisitors(totalVisitors)
                .totalRatings(totalRatings)
                .ratingsCount(ratingsCount)
                .build();
    }

    private void assertOwnership(Event event) {
        User organizer = authService.getCurrentUser();
        if(!Objects.equals(organizer.getId(), event.getOrganizer().getId()))
            throw new OwnershipRequiredException("You are not authorized to manage this event.");
    }

    @Scheduled(cron = "0 0 2 * * ?") // runs daily at 2am
    @Transactional
    public void deleteAllDrafts() {
        int deleted = repository.deleteByIsDraftTrue();
        log.info("Deleted {} drafts", deleted);
    }
}
