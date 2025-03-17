package com.iss.eventorium.event.provider;

import com.iss.eventorium.user.dtos.auth.LoginRequestDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class AccountEventProvider {

    public static Stream<Arguments> provideOrganizerEvents() {
        return Stream.of(
                Arguments.of(new LoginRequestDto("organizer@gmail.com", "pera"), 2),
                Arguments.of(new LoginRequestDto("organizer2@gmail.com", "pera"), 2),
                Arguments.of(new LoginRequestDto("organizernoevents@gmail.com", "pera"), 0)
        );
    }
}
