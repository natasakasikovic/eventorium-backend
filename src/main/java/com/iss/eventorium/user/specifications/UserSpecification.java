package com.iss.eventorium.user.specifications;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> filterByEventAttendance(Long eventId) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Person> personJoin = root.join("person");
            Join<Person, Event> eventJoin = personJoin.join("attendingEvents");
            return criteriaBuilder.equal(eventJoin.get("id"), eventId);
        };
    }
}
