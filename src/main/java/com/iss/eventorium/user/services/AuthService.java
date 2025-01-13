package com.iss.eventorium.user.services;

import com.iss.eventorium.user.exceptions.UserSuspendedException;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return userRepository.findByEmail(((UserDetails) principal).getUsername()).orElse(null);
            }
        }
        return null;
    }

    public void isSuspended(User user) {
        if (user.getSuspended() == null)
            return;

        long differenceInMinutes = ChronoUnit.MINUTES.between(user.getSuspended(), LocalDateTime.now());
        long remainingMinutes = 72 * 60 - differenceInMinutes;

        if (remainingMinutes <= 0)
            return;

        String message = getMessage(remainingMinutes);

        throw new UserSuspendedException(message);
    }

    private static String getMessage(long remainingMinutes) {
        long remainingHours = remainingMinutes / 60;
        long remainingMinutesAfterHours = remainingMinutes % 60;

        String message = "Your account has been temporarily suspended due to policy violations! "; // TODO: think about moving to constants

        if (remainingHours == 0)
            message += "It will be reactivated in less than 1 hour.";
        else
            message += String.format("It will be reactivated in %d hours and %d minutes.", remainingHours, remainingMinutesAfterHours);
        return message;
    }

}
