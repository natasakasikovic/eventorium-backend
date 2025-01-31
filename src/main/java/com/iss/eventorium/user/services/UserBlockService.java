package com.iss.eventorium.user.services;

import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserBlock;
import com.iss.eventorium.user.repositories.UserBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBlockService {

    private final UserBlockRepository repository;
    private final UserService userService;
    private final AuthService authService;

    public void blockUser(Long id) {
        User blocker = authService.getCurrentUser();
        User blocked = userService.findUser(id);

        saveUserBlock(blocker, blocked);
        saveUserBlock(blocked, blocker);

        // TODO: remove -> 1.) favourite events, 2.) attending events
        // TODO: remove -> 1.) favourite products, 2.) favourite services
        // TODO: cancel reservations, purchased products?
    }

    private void saveUserBlock(User blocker, User blocked) {
        UserBlock block = new UserBlock(blocker, blocked);
        repository.save(block);
    }
}