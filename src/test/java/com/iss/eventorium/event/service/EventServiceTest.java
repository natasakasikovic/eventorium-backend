package com.iss.eventorium.event.service;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.event.EventRequestDto;
import com.iss.eventorium.event.dtos.event.EventResponseDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.mappers.EventMapper;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.dtos.CityDto;
import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    private City city;
    private EventRequestDto request;
    private Event event;

    @BeforeEach
    void setUp() {
        User currentUser = new User();
        currentUser.setId(123L);

        CityDto cityDto = new CityDto(123L, "Belgrade");
        city = new City(123L, "Belgrade");

        request = new EventRequestDto("First Event", "Test", LocalDate.now().plusDays(3), Privacy.OPEN, 10, null, cityDto, "Street 7");

        event = new Event(123L, "First Event", "Test", LocalDate.now().plusDays(3), Privacy.OPEN, 10, null, city, "Street 7", currentUser, null, true, null, null);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(eventMapper.fromRequest(request)).thenReturn(event);
    }

    @Test
    void givenValidInput_whenCreateEvent_thenEventIsSaved() {
        eventService.createEvent(request);
        verify(eventRepository, times(1)).save(eventCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        Assertions.assertThat(savedEvent.getId()).isEqualTo(123L);
        Assertions.assertThat(savedEvent.getName()).isEqualTo("First Event");
    }

    @Test
    void givenCurrentUser_whenCreateEvent_thenOrganizerIsAssignedAndEventSaved() {
        eventService.createEvent(request);
        verify(eventRepository, times(1)).save(eventCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        Assertions.assertThat(savedEvent.getOrganizer().getId()).isEqualTo(123L);
    }

    @Test
    void givenValidInput_whenCreateEvent_thenReturnsMappedResponse() {
        EventResponseDto response = new EventResponseDto(
                123L, "First Event", "Test", LocalDate.now().plusDays(3),
                Privacy.OPEN, 10, null, city, "Street 7", null);

        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toResponse(any(Event.class))).thenReturn(response);

        EventResponseDto result = eventService.createEvent(request);

        verify(eventRepository).save(any(Event.class));
        verify(eventMapper).toResponse(any(Event.class));
        Assertions.assertThat(result).isEqualTo(response);
    }

    @Test
    void givenValidInput_whenCreateEvent_thenAuthServiceCalledOnceToGetCurrentUser() {
        eventService.createEvent(request);
        verify(authService, times(1)).getCurrentUser();
    }

    @Test
    void givenValidInput_whenCreateEvent_thenCouldGetSuggestedCategoriesFromSelectedEventType() {
        CategoryResponseDto category = new CategoryResponseDto();
        EventTypeResponseDto type = EventTypeResponseDto.builder().suggestedCategories(List.of(category)).build();
        EventResponseDto response = EventResponseDto.builder().type(type).build();

        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toResponse(any(Event.class))).thenReturn(response);

        EventResponseDto result = eventService.createEvent(request);

        Assertions.assertThat(result.getType().getSuggestedCategories())
                .containsExactly(category);
    }
}
