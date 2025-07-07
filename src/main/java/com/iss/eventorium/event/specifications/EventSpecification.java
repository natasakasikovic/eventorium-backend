package com.iss.eventorium.event.specifications;

import com.iss.eventorium.event.dtos.event.EventFilterDto;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EventSpecification {

    private EventSpecification() {}

    public static Specification<Event> filterFutureEvents(User organizer) {
        return Specification.where(filterByOrganizer(organizer))
                .and(isNotDrafted())
                .and(hasDateAfter(LocalDate.now()));
    }

    public static Specification<Event> filterBy(EventFilterDto filter, User user) {
        return Specification
                .where(hasName(filter.getName()))
                .and(hasDescription(filter.getDescription()))
                .and(hasEventType(filter.getType()))
                .and(hasCity(filter.getCity()))
                .and(hasMaxParticipants(filter.getMaxParticipants()))
                .and(hasDateAfter(filter.getFrom()))
                .and(hasDateBefore(filter.getTo()))
                .and(hasPrivacy(Privacy.OPEN))
                .and(isNotDrafted())
                .and(filterOutBlockedContent(user));
    }

    public static Specification<Event> filterByPrivacy(Privacy privacy, User user) {
        return Specification.where(hasPrivacy(privacy)
                .and(isNotDrafted())
                .and(filterOutBlockedContent(user)));
    }

    public static Specification<Event> filterByName(String keyword, User user) {
        return Specification.where(hasName(keyword))
                            .and(hasPrivacy(Privacy.OPEN)
                            .and(isNotDrafted())
                            .and(filterOutBlockedContent(user)));
    }

    public static Specification<Event> filterTopEvents(String city, User user){
        return Specification.where(hasPrivacy(Privacy.OPEN)
                            .and(hasCity(city))
                            .and(isNotDrafted())
                            .and(hasDateAfter(LocalDate.now())) // only events in future
                            .and(filterOutBlockedContent(user)));
    }

    public static Specification<Event> filterByOrganizer(User organizer) {
        return Specification.where(hasOrganizer(organizer))
                .and(isNotDrafted());
    }

    public static Specification<Event> filterUpcomingEventsByOrganizer(User organizer) {
        return Specification.where(hasOrganizer(organizer))
                .and(isNotDrafted())
                .and(hasDateAfter(LocalDate.now()));
    }

    public static Specification<Event> filterPassedEvents() {
        return Specification.where(hasDateBefore(LocalDate.now()))
                .and(isNotDrafted());
    }

    public static Specification<Event> filterPassedEventsByOrganizer(User organizer) {
        return Specification.where(hasDateBefore(LocalDate.now()))
                .and(isNotDrafted())
                .and(hasOrganizer(organizer));
    }

    public static Specification<Event> filterByNameForOrganizer(String keyword, User user) {
        return Specification.where(hasName(keyword))
                .and(isNotDrafted())
                .and(hasOrganizer(user));
    }

    public static Specification<Event> filterById(Long id, User user) {
        return Specification.where(hasId(id)
                .and(isNotDrafted())
                .and(filterOutBlockedContent(user)));
    }

    private static Specification<Event> hasOrganizer(User organizer) {
        return (root, query, cb) -> cb.equal(root.get("organizer").get("id"), organizer.getId());
    }

    private static Specification<Event> hasId(Long id){
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    private static Specification<Event> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Event> isNotDrafted() {
       return (root, query, cb) -> cb.isFalse(root.get("isDraft"));
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
                        : cb.equal(cb.lower(root.get("type").get("name")), eventType.toLowerCase());
    }

    private static Specification<Event> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isEmpty())
                return cb.conjunction();
            return cb.like(cb.lower(root.get("city").get("name")), "%" + city.toLowerCase() + "%");
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
                cb.equal(root.get("privacy"), privacy);
    }

    private static Specification<Event> filterOutBlockedContent(User blocker) {
        return (root, query, cb) -> {
            if (blocker == null) return cb.conjunction();

            Long blockerId = blocker.getId();

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserBlock> userBlockRoot = subquery.from(UserBlock.class);

            subquery.select(userBlockRoot.get("blocked").get("id"))
                    .where(cb.equal(userBlockRoot.get("blocker").get("id"), blockerId));

            return cb.not(root.get("organizer").get("id").in(subquery));
        };
    }
}