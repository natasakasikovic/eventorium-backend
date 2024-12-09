package com.iss.eventorium.user.services;

import com.iss.eventorium.security.jwt.JwtTokenUtil;
import com.iss.eventorium.user.dtos.GetAccountDto;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) throw new UsernameNotFoundException("User not found with given email: " + username);

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.get().getPassword())
                .roles(user.get().getRole().toString())
                .build();
    }

    public GetAccountDto getUserByEmail(String email) throws EntityNotFoundException, AccessDeniedException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found with given email"));
        if (!user.isActivated()) { throw new AccessDeniedException("Account is not activated"); }

        Person person = user.getPerson();
        GetAccountDto accountDto = new GetAccountDto(
                user.getId(),
                user.getEmail(),
                person.getName(),
                person.getLastname(),
                person.getPhoneNumber(),
                person.getAddress(),
                person.getCity().getName(),
                user.getRole(),
                jwtTokenUtil.generateToken(user.getEmail(), user.getRole())
        );
        return accountDto;
    }
}
