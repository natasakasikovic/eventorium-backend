package com.iss.eventorium.user.controllers;

import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.user.dtos.auth.UserTokenState;
import com.iss.eventorium.user.dtos.user.AccountDetailsDto;
import com.iss.eventorium.user.dtos.user.ChangePasswordRequestDto;
import com.iss.eventorium.user.dtos.user.UpdateRequestDto;
import com.iss.eventorium.user.dtos.user.UpgradeAccountRequestDto;
import com.iss.eventorium.user.exceptions.InvalidOldPasswordException;
import com.iss.eventorium.user.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) throws InvalidOldPasswordException {
        service.changePassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@RequestBody UpdateRequestDto person) {
        service.update(person);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AccountDetailsDto> getCurrentUser() {
        return ResponseEntity.ok(service.getCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDetailsDto> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getUser(id));
    }

    @GetMapping("/{id}/profile-photo")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable("id") Long id) {
        ImagePath path = service.getProfilePhotoPath(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(path.getContentType()))
                .body(service.getProfilePhoto(path));
    }

    @PutMapping(value = "/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> uploadProfilePhoto(@RequestParam("profilePhoto") MultipartFile file) {
        service.updateProfilePhoto(file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateAccount() {
        service.deactivateAccount();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/account-role")
    public ResponseEntity<UserTokenState> upgradeAccount(@RequestBody UpgradeAccountRequestDto request) {
        return ResponseEntity.ok(service.upgradeAccount(request));
    }
}
