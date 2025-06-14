package com.iss.eventorium.user.services;

import com.iss.eventorium.security.utils.JwtTokenUtil;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.exceptions.ImageUploadException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.utils.HashUtils;
import com.iss.eventorium.shared.services.ImageService;
import com.iss.eventorium.user.dtos.auth.AuthRequestDto;
import com.iss.eventorium.user.dtos.auth.AuthResponseDto;
import com.iss.eventorium.user.dtos.auth.QuickRegistrationRequestDto;
import com.iss.eventorium.user.dtos.auth.UserTokenState;
import com.iss.eventorium.user.dtos.user.AccountDetailsDto;
import com.iss.eventorium.user.dtos.user.ChangePasswordRequestDto;
import com.iss.eventorium.user.dtos.user.UpdateRequestDto;
import com.iss.eventorium.user.dtos.user.UpgradeAccountRequestDto;
import com.iss.eventorium.user.exceptions.ActivationTimeoutException;
import com.iss.eventorium.user.exceptions.EmailAlreadyTakenException;
import com.iss.eventorium.user.exceptions.InvalidOldPasswordException;
import com.iss.eventorium.user.exceptions.RegistrationRequestAlreadySentException;
import com.iss.eventorium.user.mappers.PersonMapper;
import com.iss.eventorium.user.mappers.UserMapper;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import com.iss.eventorium.user.specifications.UserSpecification;
import com.iss.eventorium.user.validators.deactivation.AccountDeactivationValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final RoleService roleService;
    private final AccountActivationService accountActivationService;
    private final AuthService authService;
    private final PersonMapper personMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountDeactivationValidator validator;
    private final ImageService imageService;

    private final UserMapper mapper;

    private static final String IMG_DIR_NAME = "profilePhotos";
    private final JwtTokenUtil jwtTokenUtil;

    public User find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    private User findByHash(String hash) {
        return repository.findByHash(hash).orElseThrow(() -> new EntityNotFoundException("Registration request not found."));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public AuthResponseDto create(AuthRequestDto authRequestDto) {
        User existingUser = repository.findByEmail(authRequestDto.getEmail()).orElse(null);

        if (existingUser == null)
            return mapper.toResponse(createNewRegistrationRequest(authRequestDto));

        checkActivationStatus(existingUser);
        checkRequestExpired(existingUser);

        return mapper.toResponse(recreateRegistrationRequest(existingUser, authRequestDto));
    }

    private User createNewRegistrationRequest(AuthRequestDto authRequestDto) {
        User created = mapper.fromRequest(authRequestDto);
        created.setHash(HashUtils.generateHash());
        created.setPassword(passwordEncoder.encode(authRequestDto.getPassword()));
        sendActivationEmail(created);
        return repository.save(created);
    }

    private void sendActivationEmail(User user) {
        if (user.getRoles().stream().noneMatch(role -> "PROVIDER".equals(role.getName())))
            accountActivationService.sendActivationEmail(user);
    }

    private void checkRequestExpired(User existingUser) {
        LocalDateTime activationTime = existingUser.getActivationTimestamp()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime expiryTime = activationTime.plusHours(24);
        LocalDateTime now = LocalDateTime.now();

        if (!now.isAfter(expiryTime))
            throw new RegistrationRequestAlreadySentException("A registration request with the given email has already been sent.");
    }

    private void checkActivationStatus(User existingUser) {
        if (existingUser.isVerified())
            throw new EmailAlreadyTakenException("This email is already taken. Please log in or activate your account via the email we sent you.");
    }

    private User recreateRegistrationRequest(User existingUser, AuthRequestDto newUserDto) {
        delete(existingUser.getId());
        createNewRegistrationRequest(newUserDto);
        return repository.save(existingUser);
    }

    public void uploadProfilePhoto(Long userId, MultipartFile photo) {
        if (photo == null || photo.isEmpty()) return;

        User user = find(userId);
        String fileName = imageService.generateFileName(photo);

        try {
            imageService.uploadImage(IMG_DIR_NAME, fileName, photo);
            saveProfilePhoto(user, fileName);
        } catch (IOException e) {
            throw new ImageUploadException("Failed to save profile photo.");
        }
    }

    public void activateAccount(String hash) {
        User user = findByHash(hash);
        if (isHashValid(user.getActivationTimestamp())) {
            user.setVerified(true);
            repository.save(user);
        }
        else throw new ActivationTimeoutException("Activation time has expired.");
    }

    public void quickRegister(QuickRegistrationRequestDto request) {
        User user = mapper.fromRequest(request);
        setUserDetails(user);

        if (repository.existsByEmail(user.getEmail()))
            throw new EmailAlreadyTakenException("Registration with this email address has already been completed. Please log in to access the application.");

        repository.save(user);
    }

    private void setUserDetails(User user) {
        user.setRoles(roleService.findByName("USER"));
        user.setVerified(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private boolean isHashValid(Date activationTimestamp) {
        return (System.currentTimeMillis() - activationTimestamp.getTime()) / (1000 * 60 * 60) < 24;
    }

    public AccountDetailsDto getCurrentUser() {
        User current = authService.getCurrentUser();
        return mapper.toAccountDetails(current);
    }

    public AccountDetailsDto getUser(Long id) {
        return mapper.toAccountDetails(find(id));
    }

    public ImagePath getProfilePhotoPath(long id) {
        User user = find(id);
        if (user.getPerson().getProfilePhoto() == null)
            throw new ImageNotFoundException("Profile photo not found.");

        return user.getPerson().getProfilePhoto();
    }

    public byte[] getProfilePhoto(ImagePath path) {
        return imageService.getImage(IMG_DIR_NAME, path);
    }

    public void update(UpdateRequestDto person) {
        User user = authService.getCurrentUser();
        Person updated = personMapper.fromRequest(person);
        updated.setProfilePhoto(user.getPerson().getProfilePhoto());
        user.setPerson(updated);
        repository.save(user);
    }

    private void saveProfilePhoto(User user, String fileName) throws IOException {
        String contentType = imageService.getImageContentType(IMG_DIR_NAME, fileName);
        ImagePath path = ImagePath.builder().path(fileName).contentType(contentType).build();
        user.getPerson().setProfilePhoto(path);
        repository.save(user);
    }

    public void updateProfilePhoto(MultipartFile photo) {
        uploadProfilePhoto(this.getCurrentUser().getId(), photo);
    }

    public void changePassword(ChangePasswordRequestDto request) throws InvalidOldPasswordException {
        User user = authService.getCurrentUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new InvalidOldPasswordException("Old password does not match.");

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLastPasswordReset(new Date());
        repository.save(user);
    }

    public void cleanUserOfBlockedOrganizerContent(User user, User organizer) {
        user.getPerson().getFavouriteEvents().removeIf(event -> event.getOrganizer().equals(organizer));
        user.getPerson().getAttendingEvents().removeIf(event -> event.getOrganizer().equals(organizer));
        repository.save(user);
    }

    public void cleanUserOfBlockedProviderContent(User user, User provider) {
        user.getPerson().getFavouriteProducts().removeIf(product -> product.getProvider().equals(provider));
        user.getPerson().getFavouriteServices().removeIf(service -> service.getProvider().equals(provider));
        repository.save(user);
    }

    public void deactivateAccount() {
        User user = authService.getCurrentUser();
        validator.validate(user);
        user.setDeactivated(true);
        repository.save(user);
    }

    public List<User> findByEventAttendance(Long eventId) {
        Specification<User> specification = UserSpecification.filterByEventAttendance(eventId);
        return repository.findAll(specification);
    }

    public UserTokenState upgradeAccount(UpgradeAccountRequestDto request) {
        User user = authService.getCurrentUser();
        user.setRoles(new ArrayList<>(List.of(roleService.findById(request.getRole().getId()))));
        user.getPerson().setAddress(request.getAddress());
        user.getPerson().setPhoneNumber(request.getPhoneNumber());

        user = find(user.getId()); // think: is this need?

        String jwt = jwtTokenUtil.generateToken(user);
        Long expiresIn = jwtTokenUtil.getExpiresIn();
        return new UserTokenState(jwt, expiresIn);
    }
}
