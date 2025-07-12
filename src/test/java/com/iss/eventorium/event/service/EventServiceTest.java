package com.iss.eventorium.event.service;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.event.EventRequestDto;
import com.iss.eventorium.event.dtos.event.EventResponseDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.exceptions.AgendaAlreadyDefinedException;
import com.iss.eventorium.event.exceptions.EmptyAgendaException;
import com.iss.eventorium.event.mappers.ActivityMapper;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.models.Activity;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.exceptions.InvalidTimeRangeException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

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

    private EventRequestDto request;
    private Event event;
    private User currentUser;
    private final Long ORGANIZER_ID = 111L;
    private final Long EVENT_ID = 555L;

    @BeforeEach
    void setUp() {
        currentUser = User.builder().id(ORGANIZER_ID).build();
        request = EventRequestDto.builder().name("First Event").build();
        event = Event.builder().id(EVENT_ID).name("First Event").organizer(currentUser).isDraft(true).privacy(Privacy.OPEN).activities(new ArrayList<>()).build();
    }

    @Test
    @DisplayName("Successfully creates and returns event with organizer and related metadata")
    void givenValidInput_whenCreateEvent_thenAllPropertiesAreSetCorrectly() {
        CategoryResponseDto category = new CategoryResponseDto();
        EventTypeResponseDto type = EventTypeResponseDto.builder().suggestedCategories(List.of(category)).build();
        EventResponseDto expectedResponse = EventResponseDto.builder().id(EVENT_ID).name("First Event").type(type).build();

        when(eventMapper.fromRequest(request)).thenReturn(event);
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toResponse(any(Event.class))).thenReturn(expectedResponse);

        EventResponseDto result = eventService.createEvent(request);

        verify(eventRepository, times(1)).save(eventCaptor.capture());
        verify(eventMapper, times(1)).toResponse(any(Event.class));

        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getId()).isEqualTo(EVENT_ID);
        assertThat(savedEvent.getName()).isEqualTo("First Event");
        assertThat(savedEvent.getOrganizer().getId()).isEqualTo(ORGANIZER_ID);
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.getType().getSuggestedCategories()).containsExactly(category);
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

        assertThatCode(() -> eventService.createAgenda(EVENT_ID, requests))
                .doesNotThrowAnyException();
        verify(eventRepository, times(1)).save(eventCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getActivities()).containsExactlyElementsOf(activities);
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.EventProvider#provideInvalidTimeRanges")
    @Tag("create-agenda")
    @DisplayName("Should throw exception for invalid activity time range")
    void givenInvalidActivityTimeRange_whenCreateAgenda_thenThrowsInvalidTimeRangeException(LocalTime startTime,
                                                                                            LocalTime endTime,
                                                                                            String expectedMessage) {
        List<ActivityRequestDto> requests = List.of(new ActivityRequestDto());
        Activity activity = Activity.builder()
                .name("Activity1")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(activityMapper.fromRequest(requests.get(0))).thenReturn(activity);
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.of(event));

        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class,
                () -> eventService.createAgenda(EVENT_ID, requests));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("create-agenda")
    @DisplayName("Should throw exception when agenda has no activities")
    void givenNoActivities_whenCreateAgenda_shouldThrowEmptyAgendaException() {
        List<ActivityRequestDto> requests = new ArrayList<>();

        EmptyAgendaException exception = assertThrows(EmptyAgendaException.class,
                () -> eventService.createAgenda(EVENT_ID, requests));

        assertEquals("Agenda must contain at least one activity.", exception.getMessage());
    }

    @Test
    @Tag("create-agenda")
    @DisplayName("Should throw exception when agenda is already defined")
    void givenEventWithAgenda_whenCreateAgenda_shouldThrowAgendaAlreadyDefinedException() {
        event.setActivities(List.of(new Activity()));
        List<ActivityRequestDto> requests = List.of(new ActivityRequestDto());
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.of(event));
        when(authService.getCurrentUser()).thenReturn(currentUser);

        AgendaAlreadyDefinedException exception = assertThrows(AgendaAlreadyDefinedException.class,
                () -> eventService.createAgenda(EVENT_ID, requests));

        assertEquals("Agenda already defined for event with name First Event", exception.getMessage());
    }

    @Test
    @DisplayName("Should generate guest list PDF for valid event")
    void givenValidEventId_whenGenerateGuestListPdf_thenReturnPdfBytes() {

        List<User> guests = List.of(new User(), new User());
        byte[] pdfBytes = new byte[]{1, 2, 3};

        when(userService.findByEventAttendance(EVENT_ID)).thenReturn(guests);
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.of(event));
        when(pdfService.generate(anyString(), eq(guests), anyMap())).thenReturn(pdfBytes);

        byte[] result = eventService.generateGuestListPdf(EVENT_ID);

        assertNotNull(result);
        assertArrayEquals(pdfBytes, result);
        verify(userService).findByEventAttendance(EVENT_ID);
        verify(pdfService).generate(eq("/templates/guest-list-pdf.jrxml"), eq(guests), anyMap());
    }

    @Test
    @DisplayName("Should throw exception while generating guest list for event that does not exist")
    void givenInvalidEventId_whenGenerateGuestListPdf_thenThrowsEntityNotFoundException() {
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.generateGuestListPdf(EVENT_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Event not found");
    }

    @Test
    @DisplayName("Should generate empty guest list PDF when no attendees are found - there is no exception as expected")
    void shouldGeneratePdfWithNoGuestsWhenAttendanceIsEmpty() {

        when(userService.findByEventAttendance(EVENT_ID)).thenReturn(Collections.emptyList());
        when(eventRepository.findOne(any(Specification.class))).thenReturn(Optional.of(event));
        when(pdfService.generate(anyString(), eq(Collections.emptyList()), anyMap())).thenReturn(new byte[]{0, 1, 2});

        byte[] result = eventService.generateGuestListPdf(EVENT_ID);

        assertNotNull(result);
        verify(pdfService).generate(eq("/templates/guest-list-pdf.jrxml"), eq(Collections.emptyList()), anyMap());
    }

}
