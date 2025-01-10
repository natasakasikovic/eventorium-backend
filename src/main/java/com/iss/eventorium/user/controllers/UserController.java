package com.iss.eventorium.user.controllers;

import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.user.dtos.AccountDetailsDto;
import com.iss.eventorium.user.dtos.ChangePasswordRequestDto;
import com.iss.eventorium.user.dtos.UpdateRequestDto;
import com.iss.eventorium.user.exceptions.InvalidOldPasswordException;
import com.iss.eventorium.user.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) throws InvalidOldPasswordException {
        userService.changePassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@RequestBody UpdateRequestDto person) {
        userService.update(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deactivateAccount(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/me")
    public ResponseEntity<AccountDetailsDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/{id}/profile-photo")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable("id") Long id) {
        ImagePath path = userService.getProfilePhotoPath(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(path.getContentType()))
                .body(userService.getProfilePhoto(path));
    }

    @PutMapping(value = "/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> uploadProfilePhoto(@RequestParam("profilePhoto") MultipartFile file) {
        try {
            userService.updateProfilePhoto(file);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
