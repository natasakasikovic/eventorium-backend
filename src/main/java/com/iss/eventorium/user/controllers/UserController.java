package com.iss.eventorium.user.controllers;

import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.user.dtos.AccountDetailsDto;
import com.iss.eventorium.user.dtos.ResetPasswordRequestDto;
import com.iss.eventorium.user.dtos.UpdateAccountDto;
import com.iss.eventorium.user.dtos.UpdatedAccountDto;
import com.iss.eventorium.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/reset-password/{id}")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id,
                                              @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedAccountDto> updateAccount(@PathVariable Long id, @RequestBody UpdateAccountDto account) throws Exception {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deactivateAccount(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/me")
    public ResponseEntity<AccountDetailsDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/{id}/profilePhoto")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable("id") Long id) {
        ImagePath path = userService.getProfilePhotoPath(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(path.getContentType()))
                .body(userService.getProfilePhoto(path));
    }
}
