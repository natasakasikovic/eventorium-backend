package com.iss.eventorium.user.controllers;

import com.iss.eventorium.user.dtos.*;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.UserService;
import com.iss.eventorium.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    private final UserService userService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody LoginRequestDto authenticationRequest, HttpServletResponse response) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            String jwt = jwtTokenUtil.generateToken(user);
            Long expiresIn = jwtTokenUtil.getExpiredIn();

            return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAccountDto> createAccount(@RequestBody RegistrationRequestDto user) throws  Exception {
        GetAccountDto savedUser = new GetAccountDto(); // TODO: call service

        if (savedUser == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        return new ResponseEntity<GetAccountDto>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/send-activation-link")
    public ResponseEntity<String> sendActivationLink(@RequestBody ActivationRequestDto  request) {
        return ResponseEntity.ok("Activation link sent successfully.");
    }
}
