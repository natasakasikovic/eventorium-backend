package com.iss.eventorium.event.repository;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.specifications.EventSpecification;
import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.user.models.Role;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.iss.eventorium.util.EntityFactory.*;
import static com.iss.eventorium.util.TestUtil.resetTables;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        resetTables(jdbcTemplate);

        Role eventOrganizer = new Role();
        eventOrganizer.setName("EVENT_ORGANIZER");
        entityManager.persist(eventOrganizer);

        City beograd = new City();
        beograd.setName("Beograd");
        entityManager.persist(beograd);

        User user1 = createUser("organizer@gmail.com", "1", eventOrganizer);
        User user2 = createUser("organizer2@gmail.com", "2", eventOrganizer);
        User user3 = createUser("organizer3@gmail.com", "3", eventOrganizer);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        EventType wedding = createEventType("Weeding");
        entityManager.persist(wedding);

        Event event1 = createEvent("Wedding in Novi Sad", "A beautiful wedding ceremony with reception and dance.", 3, "123 Wedding St", beograd, user1, wedding);
        Event event2 = createEvent("Corporate Event in Novi Sad", "A corporate networking event with speakers and workshops.", 4, "456 Business Ave", beograd, user1, wedding);
        Event event3 = createEvent("Birthday Bash in Sombor", "A fun-filled birthday party with music and food.", 5, "789 Birthday Blvd", beograd, user1, wedding);
        Event event4 = createEvent("Sombor Business Meetup", "A professional business networking event in Sombor.", 3, "234 Business Rd", beograd, user2, wedding);
        Event event5 = createEvent("Wedding Reception in Novi Sad", "An elegant wedding reception with dinner and music.", 4, "321 Reception St", beograd, user2, wedding);
        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        entityManager.persist(event4);
        entityManager.persist(event5);

        UserBlock block = UserBlock.builder().blocker(user3).blocked(user1).build();
        entityManager.persist(block);
    }

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

}
