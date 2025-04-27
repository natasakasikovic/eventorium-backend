package com.iss.eventorium.user.controllers;

import com.iss.eventorium.user.dtos.auth.*;
import com.iss.eventorium.user.services.AuthService;
import com.iss.eventorium.user.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponseDto> createAccount(@Valid @RequestBody AuthRequestDto user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @PostMapping(value = "/{userId}/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> uploadProfilePhoto(@PathVariable Long userId, @RequestParam("profilePhoto") MultipartFile file) {
        userService.uploadProfilePhoto(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/quick-registration")
    public ResponseEntity<Void> quickRegister(@Valid @RequestBody QuickRegistrationRequestDto request) {
        userService.quickRegister(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("activation/{hash}")
    public ResponseEntity<Void> activateAccount(@PathVariable String hash) {
        userService.activateAccount(hash);
        String redirectUrl = frontendUrl + "/login";
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", redirectUrl)
                .build();
    }
}
