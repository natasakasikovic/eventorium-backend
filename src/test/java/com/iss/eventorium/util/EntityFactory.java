package com.iss.eventorium.util;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.Role;
import com.iss.eventorium.user.models.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntityFactory {

    public static User createUser(String email, String hash, Role role) {
        return User.builder()
                .person(createPerson())
                .email(email)
                .password("$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W") // pera
                .verified(true)
                .activationTimestamp(new Date(1733572800000L))
                .lastPasswordReset(new Date(1506923938508L))
                .deactivated(false)
                .hash(hash)
                .roles(List.of(role))
                .build();
    }

    public static Event createEvent(String name, String description, int daysFromNow, String address, City city, User organizer, EventType type) {
        return Event.builder()
                .name(name)
                .description(description)
                .date(LocalDate.now().plusDays(daysFromNow))
                .privacy(Privacy.OPEN)
                .maxParticipants(100)
                .type(type)
                .city(city)
                .address(address)
                .activities(new ArrayList<>())
                .organizer(organizer)
                .isDraft(false)
                .build();
    }

    public static EventType createEventType(String name) {
        return EventType.builder()
                .name(name)
                .description("Event type description")
                .deleted(false)
                .build();
    }

    private static Person createPerson() {
        return Person.builder()
                .name("John")
                .lastname("Doe")
                .build();
    }

}
