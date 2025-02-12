package com.iss.eventorium.event.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventDateChangedEvent extends ApplicationEvent {
    private final Long eventId;

    public EventDateChangedEvent(Object source, Long eventId) {
        super(source);
        this.eventId = eventId;
    }
}
