package com.iss.eventorium.user.services;

import com.iss.eventorium.shared.utils.HashUtils;
import com.iss.eventorium.user.controllers.ReportController;
import com.iss.eventorium.user.dtos.QuickRegistrationRequestDto;
import com.iss.eventorium.shared.utils.ImageUpload;
import com.iss.eventorium.user.dtos.AuthRequestDto;
import com.iss.eventorium.user.dtos.AuthResponseDto;
import com.iss.eventorium.user.mappers.UserMapper;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AccountActivationService accountActivationService;

    @Value("${image-path}")
    private String imagePath;

    public User findByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void quickRegister(QuickRegistrationRequestDto request){
        User user = UserMapper.fromRequest(request);
        setUserDetails(user);

        if (userRepository.existsByEmail(user.getEmail()))
            throw new DuplicateKeyException("Account with given email already exists.");

        userRepository.save(user);
    }

    private void setUserDetails(User user) {
        user.setRoles(roleService.findByName("USER"));
        user.setActivated(true);
        user.setPassword(encodePassword(user.getPassword()));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public AuthResponseDto create(AuthRequestDto authRequestDto) throws DuplicateKeyException, IllegalStateException {
        User existingUser = userRepository.findByEmail(authRequestDto.getEmail()).orElse(null);

        if (existingUser == null) {
            return UserMapper.toResponse(createNewRegistrationRequest(authRequestDto));
        }

        checkActivationStatus(existingUser);
        checkRequestExpired(existingUser);

        return UserMapper.toResponse(recreateRegistrationRequest(existingUser, authRequestDto));
    }

    private User createNewRegistrationRequest(AuthRequestDto authRequestDto) {
        User created = UserMapper.fromRequest(authRequestDto);
        created.setHash(HashUtils.generateHash());
        created.setPassword(encodePassword(authRequestDto.getPassword()));
        accountActivationService.sendActivationEmail(created);
        return userRepository.save(created);
    }

    private String encodePassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
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

    private User recreateRegistrationRequest(User existingUser, AuthRequestDto newUserDto) {
        delete(existingUser.getId());
        createNewRegistrationRequest(newUserDto);
        return userRepository.save(existingUser);
    }

    public void uploadProfilePhoto(Long userId, MultipartFile photo) {
        if (photo == null) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not fount."));

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(photo.getOriginalFilename()));
        String uploadDir = StringUtils.cleanPath(imagePath + "profilePhotos/");

        try {
            ImageUpload.saveImage(uploadDir, fileName, photo);
            user.getPerson().setProfilePhoto(fileName);
            userRepository.save(user);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save the photo.", e);
        }
    }

    private User findByHash(String hash) {
        return userRepository.findByHash(hash).orElseThrow(() -> new EntityNotFoundException("User not fount."));
    }

    public void activateAccount(String hash) throws TimeoutException {
        User user = findByHash(hash);
        if (isHashValid(user.getActivationTimestamp())) {
            user.setActivated(true);
            userRepository.save(user);
        }
        else throw new TimeoutException("Activation time has expired.");
    }

    private boolean isHashValid(Date activationTimestamp) {
        return (System.currentTimeMillis() - activationTimestamp.getTime()) / (1000 * 60 * 60) < 24;
    }

}
