package com.iss.eventorium.event.repository;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.specifications.EventSpecification;
import com.iss.eventorium.user.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/event-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TestEntityManager entityManager;

    @ParameterizedTest(name = "Organizer ID: {0} should have {1} events")
    @DisplayName("Should return correct number of events for each organizer")
    @CsvSource({
         "1,3",
         "2,2",
         "3,0"
    })
    void givenOrganizerId_whenFilterByOrganizer_thenReturnExpectedEventCount(Long organizerId, int expected) {
        User organizer = entityManager.find(User.class, organizerId);
        List<Event> events = eventRepository.findAll(EventSpecification.filterByOrganizer(organizer));
        assertNotNull(events);
        assertEquals(expected, events.size());
    }

    @Test
    @DisplayName("Should return event when user is not blocking the organizer")
    void givenExistingEventAndNonBlockingUser_whenFilterById_thenReturnsEvent() {
        User organizer = entityManager.find(User.class, 1L);
        Event event = eventRepository.findAll().get(0);

        Specification<Event> spec = EventSpecification.filterById(event.getId(), organizer);
        Optional<Event> result = eventRepository.findOne(spec);

        assertTrue(result.isPresent());
        assertEquals(event.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should not return event when user has blocked the organizer")
    void givenExistingEventAndBlockedOrganizer_whenFilterById_thenReturnsEmpty() {
        User user3 = entityManager.find(User.class, 3L);
        Event event = eventRepository.findById(1L).orElseThrow();

        Specification<Event> spec = EventSpecification.filterById(event.getId(), user3);
        Optional<Event> result = eventRepository.findOne(spec);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when event with given ID does not exist")
    void givenNonExistentEventId_whenFilterById_thenReturnsEmpty() {
        User user = entityManager.find(User.class, 1L);
        Long nonExistentId = 999L;

        Specification<Event> spec = EventSpecification.filterById(nonExistentId, user);
        Optional<Event> result = eventRepository.findOne(spec);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return only CLOSED events")
    void givenPrivacyOpen_whenFindAllWithSpec_thenOnlyOpenEventsReturned() {
        Specification<Event> spec = EventSpecification.filterByPrivacy(Privacy.OPEN, null);
        List<Event> events = eventRepository.findAll(spec);
        assertEquals(4, events.size());
        assertTrue(events.stream().allMatch(event -> event.getPrivacy() == Privacy.OPEN));
    }

}
