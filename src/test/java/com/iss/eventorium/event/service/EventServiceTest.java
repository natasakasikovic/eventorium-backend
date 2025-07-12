package com.iss.eventorium.event.service;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.event.EventRequestDto;
import com.iss.eventorium.event.dtos.event.EventResponseDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.exceptions.EmptyAgendaException;
import com.iss.eventorium.event.mappers.ActivityMapper;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.models.Activity;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.dtos.CityDto;
import com.iss.eventorium.shared.exceptions.InvalidTimeRangeException;
import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.shared.services.PdfService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import com.iss.eventorium.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private PdfService pdfService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private ActivityMapper activityMapper;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    private City city;
    private EventRequestDto request;
    private Event event;
    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = User.builder().id(123L).build();

        CityDto cityDto = new CityDto(123L, "Belgrade");
        city = new City(123L, "Belgrade");

        request = new EventRequestDto("First Event", "Test", LocalDate.now().plusDays(3), Privacy.OPEN, 10, null, cityDto, "Street 7");

        event = new Event(123L, "First Event", "Test", LocalDate.now().plusDays(3), Privacy.OPEN, 10, null, city, "Street 7", currentUser, null, true, null, null);
    }

    @Test
    @Tag("create-event")
    @DisplayName("Should save event when input is valid")
    void givenValidInput_whenCreateEvent_thenEventIsSaved() {
        when(eventMapper.fromRequest(request)).thenReturn(event);
        eventService.createEvent(request);
        verify(eventRepository, times(1)).save(eventCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getId()).isEqualTo(123L);
        assertThat(savedEvent.getName()).isEqualTo("First Event");
    }

    @Test
    @Tag("create-event")
    @DisplayName("Should assign current user as organizer")
    void givenCurrentUser_whenCreateEvent_thenOrganizerIsAssignedAndEventSaved() {
        when(eventMapper.fromRequest(request)).thenReturn(event);
        when(authService.getCurrentUser()).thenReturn(currentUser);
        eventService.createEvent(request);
        verify(eventRepository, times(1)).save(eventCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getOrganizer().getId()).isEqualTo(123L);
    }

    @Test
    @Tag("create-event")
    @DisplayName("Should return mapped response when event is created with valid input")
    void givenValidInput_whenCreateEvent_thenReturnsMappedResponse() {
        when(eventMapper.fromRequest(request)).thenReturn(event);
        EventResponseDto response = new EventResponseDto(
                123L, "First Event", "Test", LocalDate.now().plusDays(3),
                Privacy.OPEN, 10, null, city, "Street 7", null);

        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toResponse(any(Event.class))).thenReturn(response);

        EventResponseDto result = eventService.createEvent(request);

        verify(eventRepository).save(any(Event.class));
        verify(eventMapper).toResponse(any(Event.class));
        assertThat(result).isEqualTo(response);
    }

    @Test
    @Tag("create-event")
    @DisplayName("Create event should include suggested categories from its event type in response")
    void givenValidInput_whenCreateEvent_thenCouldGetSuggestedCategoriesFromSelectedEventType() {
        CategoryResponseDto category = new CategoryResponseDto();
        EventTypeResponseDto type = EventTypeResponseDto.builder().suggestedCategories(List.of(category)).build();
        EventResponseDto response = EventResponseDto.builder().type(type).build();

        when(eventMapper.fromRequest(request)).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toResponse(any(Event.class))).thenReturn(response);

        EventResponseDto result = eventService.createEvent(request);

        assertThat(result.getType().getSuggestedCategories())
                .containsExactly(category);
    }

    @Test
    @Tag("create-agenda")
    @DisplayName("Should update and save event with given valid agenda data")
    void givenValidActivities_whenCreateAgenda_thenEventIsUpdatedAndSavedCorrectly() {

        List<ActivityRequestDto> requests = List.of(new ActivityRequestDto(), new ActivityRequestDto());

        List<Activity> activities = List.of(
            Activity.builder().startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(10, 0)).build(),
            Activity.builder().startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(11, 0)).build()
        );

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(activityMapper.fromRequest(requests.get(0))).thenReturn(activities.get(0));
        when(activityMapper.fromRequest(requests.get(1))).thenReturn(activities.get(1));
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.of(event));

        assertThatCode(() -> eventService.createAgenda(123L, requests))
                .doesNotThrowAnyException();
        verify(eventRepository, times(1)).save(eventCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getActivities()).containsExactlyElementsOf(activities);
    }

    @Test
    @Tag("create-agenda")
    @DisplayName("Should throw exception for activity with end time before start time")
    void givenActivityWithEndTimeBeforeStartTime_whenCreateAgenda_thenThrowsInvalidTimeRangeException() {

        List<ActivityRequestDto> requests = List.of(new ActivityRequestDto());
        List<Activity> activities = List.of(
                Activity.builder().name("Activity1").startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(9, 0)).build()
        );

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(activityMapper.fromRequest(requests.get(0))).thenReturn(activities.get(0));
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.of(event));

        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> eventService.createAgenda(123L, requests));
        assertEquals("Invalid time range for activity 'Activity1': end time (09:00) must be after start time (10:00).", exception.getMessage());
    }

    @Test
    @Tag("create-agenda")
    @DisplayName("Should throw exception for activity with end time equals to start time")
    void givenActivityWithEndTimeEqualsToStartTime_whenCreateAgenda_thenThrowsInvalidTimeRangeException() {

        List<ActivityRequestDto> requests = List.of(new ActivityRequestDto());
        List<Activity> activities = List.of(
                Activity.builder().name("Activity1").startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(10, 0)).build()
        );

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(activityMapper.fromRequest(requests.get(0))).thenReturn(activities.get(0));
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.of(event));

        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> eventService.createAgenda(123L, requests));
        assertEquals("Invalid time range for activity 'Activity1': end time (10:00) must be after start time (10:00).", exception.getMessage());
    }

    @Test
    @Tag("create-agenda")
    @DisplayName("Should throw EmptyAgendaException when agenda has no activities")
    void givenNoActivities_whenCreateAgenda_shouldThrowEmptyAgendaException() {
        List<ActivityRequestDto> requests = new ArrayList<>();

        EmptyAgendaException exception = assertThrows(EmptyAgendaException.class, () -> eventService.createAgenda(123L, requests));
        assertEquals("Agenda must contain at least one activity.", exception.getMessage());
    }

    @Test
    @DisplayName("Should generate guest list PDF for valid event")
    void givenValidEventId_whenGenerateGuestListPdf_thenReturnPdfBytes() {

        List<User> guests = List.of(new User(), new User());
        byte[] pdfBytes = new byte[]{1, 2, 3};

        when(userService.findByEventAttendance(123L)).thenReturn(guests);
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.of(event));
        when(pdfService.generate(anyString(), eq(guests), anyMap())).thenReturn(pdfBytes);

        byte[] result = eventService.generateGuestListPdf(123L);

        assertNotNull(result);
        assertArrayEquals(pdfBytes, result);
        verify(userService).findByEventAttendance(123L);
        verify(pdfService).generate(eq("/templates/guest-list-pdf.jrxml"), eq(guests), anyMap());
    }

    @Test
    @DisplayName("Should throw exception while generating guest list for event that does not exist")
    void givenInvalidEventId_whenGenerateGuestListPdf_thenThrowsEntityNotFoundException() {
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.generateGuestListPdf(123L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Event not found");
    }

    @Test
    @DisplayName("Should generate empty guest list PDF when no attendees are found")
    void shouldGeneratePdfWithNoGuestsWhenAttendanceIsEmpty() {

        when(userService.findByEventAttendance(123L)).thenReturn(Collections.emptyList());
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.of(event));
        when(pdfService.generate(anyString(), eq(Collections.emptyList()), anyMap())).thenReturn(new byte[]{0, 1, 2});

        byte[] result = eventService.generateGuestListPdf(123L);

        assertNotNull(result);
        verify(pdfService).generate(eq("/templates/guest-list-pdf.jrxml"), eq(Collections.emptyList()), anyMap());
    }

}
