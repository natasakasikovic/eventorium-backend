package com.iss.eventorium.user.services;

import com.iss.eventorium.user.dtos.AuthRequestDto;
import com.iss.eventorium.user.mappers.UserMapper;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public User findById(Long id) throws AccessDeniedException {
        return userRepository.findById(id).orElseGet(null);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public User createAccount(AuthRequestDto user) {

        User created = UserMapper.fromRequest(user);

        created.setActivated(false);
        created.setSuspended(false);
        created.setActivationTimestamp(new Date());
        created.setLastPasswordReset(new Date());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        created.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(created);
    }
}
