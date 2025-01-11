package com.iss.eventorium.user.services;

import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.utils.HashUtils;
import com.iss.eventorium.user.dtos.*;
import com.iss.eventorium.shared.utils.ImageUpload;
import com.iss.eventorium.user.exceptions.InvalidOldPasswordException;
import com.iss.eventorium.user.mappers.PersonMapper;
import com.iss.eventorium.user.mappers.UserMapper;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
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
    private final AuthService authService;
    private final PersonMapper personMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${image-path}")
    private String imagePath;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
        created.setPassword(passwordEncoder.encode(authRequestDto.getPassword()));
        sendActivationEmail(created);
        return userRepository.save(created);
    }

    private void sendActivationEmail(User user) {
        if (user.getRoles().stream().noneMatch(role -> "PROVIDER".equals(role.getName())))
            accountActivationService.sendActivationEmail(user);
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

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(photo.getOriginalFilename()));
        String fileName = Instant.now().toEpochMilli() + "_" + originalFileName;
        String uploadDir = StringUtils.cleanPath(imagePath + "profilePhotos/");

        try {
            ImageUpload.saveImage(uploadDir, fileName, photo);
            String contentType = ImageUpload.getImageContentType(uploadDir, fileName);
            user.getPerson().setProfilePhoto(ImagePath.builder().path(fileName).contentType(contentType).build());
            userRepository.save(user);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save the photo.", e);
        }
    }

    public void updateProfilePhoto(MultipartFile photo) {
        uploadProfilePhoto(this.getCurrentUser().getId(), photo);
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

    public AccountDetailsDto getCurrentUser() {
        User current = authService.getCurrentUser();
        return UserMapper.toAccountDetailsDto(current);
    }

    public AccountDetailsDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not fount."));
        return UserMapper.toAccountDetailsDto(user);
    }

    public ImagePath getProfilePhotoPath(long id) {
        User user = findById(id);
        if (user.getPerson().getProfilePhoto() == null) {
            throw new ImageNotFoundException("Profile photo not found.");
        }
        return user.getPerson().getProfilePhoto();
    }

    public byte[] getProfilePhoto(ImagePath path) {
        String uploadDir = StringUtils.cleanPath(imagePath + "profilePhotos/");
        try {
            File file = new File(uploadDir + path.getPath());
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new ImageNotFoundException("Fail to read image");
        }
    }

    public void update(UpdateRequestDto person) {
        User user = authService.getCurrentUser();
        Person updated = personMapper.fromRequest(person);
        updated.setProfilePhoto(user.getPerson().getProfilePhoto());
        user.setPerson(updated);
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequestDto request) throws InvalidOldPasswordException {
        User user = authService.getCurrentUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidOldPasswordException("Old password does not match.");
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLastPasswordReset(new Date());
        userRepository.save(user);
    }
}
