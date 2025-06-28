package com.iss.eventorium.event.provider;

import com.iss.eventorium.user.dtos.auth.LoginRequestDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class AccountEventProvider {

    public static Stream<Arguments> provideOrganizerEvents() {
        return Stream.of(
                Arguments.of("organizer@gmail.com", 2),
                Arguments.of("organizer2@gmail.com", 2),
                Arguments.of("organizernoevents@gmail.com", 0)
        );
    }
}
