package com.iss.eventorium.user.controllers;

import com.iss.eventorium.user.dtos.*;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.UserService;
import com.iss.eventorium.utils.JwtTokenUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeoutException;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody LoginRequestDto authenticationRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            if (!user.isActivated()) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            String jwt = jwtTokenUtil.generateToken(user);
            Long expiresIn = jwtTokenUtil.getExpiredIn();

            return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponseDto> createAccount(@Valid @RequestBody AuthRequestDto user) {
        try {
            AuthResponseDto response = userService.create(user);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException | DuplicateKeyException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "/{userId}/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> uploadProfilePhoto(@PathVariable Long userId, @RequestParam("profilePhoto") MultipartFile file) {
        try {
            userService.uploadProfilePhoto(userId, file);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/quick-registration")
    public ResponseEntity<Void> quickRegister(@Valid @RequestBody QuickRegistrationRequestDto request) {
        try{
            userService.quickRegister(request);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DuplicateKeyException e)    {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("activation/{hash}")
    public ResponseEntity<Void> activateAccount(@PathVariable String hash) {
        try {
            userService.activateAccount(hash);
            String redirectUrl = FRONTEND_URL + "/login";
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", redirectUrl)
                    .build();
        } catch (TimeoutException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
