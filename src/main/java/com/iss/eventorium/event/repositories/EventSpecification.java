package com.iss.eventorium.event.repositories;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.shared.utils.EventFilter;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EventSpecification {

    public static Specification<Event> filterBy(EventFilter eventFilter) {
        return Specification
                .where(hasName(eventFilter.getName()))
                .and(hasDescription(eventFilter.getDescription()))
                .and(hasEventType(eventFilter.getEventType()))
                .and(hasLocationCity(eventFilter.getLocation()))
                .and(hasMaxParticipants(eventFilter.getMaxParticipants()))
                .and(hasDateAfter(eventFilter.getFrom()))
                .and(hasDateBefore(eventFilter.getTo()))
                .and(hasPrivacy(eventFilter.getPrivacy()));
    }

    private static Specification<Event> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Event> hasDescription(String description) {
        return (root, query, cb) ->
                description == null || description.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<Event> hasEventType(String eventType) {
        return (root, query, cb) ->
                eventType == null || eventType.isEmpty()
                        ? cb.conjunction()
                        : cb.equal(cb.lower(root.get("eventType").get("name")), eventType.toLowerCase());
    }

    private static Specification<Event> hasLocationCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("location").get("city")), "%" + city.toLowerCase() + "%");
        };
    }


    private static Specification<Event> hasMaxParticipants(Integer maxParticipants) {
        return (root, query, cb) ->
                maxParticipants == null || maxParticipants <= 0
                        ? cb.conjunction()
                        : cb.equal(root.get("maxParticipants"), maxParticipants);
    }

    private static Specification<Event> hasDateAfter(LocalDate from) {
        return (root, query, cb) ->
                from == null
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("date"), from);
    }

    private static Specification<Event> hasDateBefore(LocalDate to) {
        return (root, query, cb) ->
                to == null
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("date"), to);
    }

    private static Specification<Event> hasPrivacy(Privacy privacy) {
        return (root, query, cb) ->
                privacy == null
                        ? cb.conjunction()
                        : cb.equal(root.get("privacy"), privacy);
    }

}
