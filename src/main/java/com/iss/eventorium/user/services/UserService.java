package com.iss.eventorium.user.services;

import com.iss.eventorium.user.dtos.AuthRequestDto;
import com.iss.eventorium.user.mappers.UserMapper;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void create(AuthRequestDto authRequestDto) throws DuplicateKeyException, IllegalStateException {
        User existingUser = userRepository.findByEmail(authRequestDto.getEmail()).orElse(null);

        if (existingUser == null) {
            createNewRegistrationRequest(authRequestDto);
            return;
        }

        checkActivationStatus(existingUser);
        checkRequestExpired(existingUser);

        recreateRegistrationRequest(existingUser, authRequestDto);
    }

    private void createNewRegistrationRequest(AuthRequestDto authRequestDto) {
        User created = UserMapper.fromRequest(authRequestDto);

        prepareRegistrationRequest(created);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        created.setPassword(encoder.encode(authRequestDto.getPassword()));

        userRepository.save(created);
    }

    private void prepareRegistrationRequest(User user) {
        user.setActivated(false);
        user.setSuspended(false);
        user.setActivationTimestamp(new Date());
        user.setLastPasswordReset(new Date());
    }

    private void checkRequestExpired(User existingUser) throws IllegalStateException {
        LocalDateTime activationTime = existingUser.getActivationTimestamp()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime expiryTime = activationTime.plusHours(24);
        LocalDateTime now = LocalDateTime.now();

        if (!now.isAfter(expiryTime)) throw new IllegalStateException("A registration request with the given email has already been sent.");
    }

    private void checkActivationStatus(User existingUser) throws DuplicateKeyException {
        if (existingUser.isActivated()) {
            throw new DuplicateKeyException("Account with given email already exists.");
        }
    }

    private void recreateRegistrationRequest(User existingUser, AuthRequestDto newUserDto) {
        delete(existingUser.getId());
        createNewRegistrationRequest(newUserDto);
        userRepository.save(existingUser);
    }
}
