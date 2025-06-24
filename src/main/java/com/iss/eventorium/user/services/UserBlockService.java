package com.iss.eventorium.user.services;

import com.iss.eventorium.user.exceptions.SelfBlockNotAllowedException;
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
        User blocked = userService.find(id);

        if (blocker.getId().equals(id))
            throw new SelfBlockNotAllowedException("User cannot block themselves.");

        saveUserBlock(blocker, blocked);
        saveUserBlock(blocked, blocker);

        cleanUserOfBlockedContent(blocker, blocked);
        cleanUserOfBlockedContent(blocked, blocker);
    }

    private void cleanUserOfBlockedContent(User blocker, User blocked) {
        if (blocked.getRoles().stream().anyMatch(role -> "EVENT_ORGANIZER".equals(role.getName())))
            userService.cleanUserOfBlockedOrganizerContent(blocker, blocked);
        else if (blocked.getRoles().stream().anyMatch(role -> "PROVIDER".equals(role.getName())))
            userService.cleanUserOfBlockedProviderContent(blocker, blocked);
    }

    private void saveUserBlock(User blocker, User blocked) {
        UserBlock block = new UserBlock(blocker, blocked);
        repository.save(block);
    }
}