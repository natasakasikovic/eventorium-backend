package com.iss.eventorium.user.controllers;

import com.iss.eventorium.security.jwt.JwtTokenUtil;
import com.iss.eventorium.user.dtos.ActivationRequestDto;
import com.iss.eventorium.user.dtos.GetAccountDto;
import com.iss.eventorium.user.dtos.LoginRequestDto;
import com.iss.eventorium.user.dtos.RegistrationRequestDto;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final UserService userService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAccountDto> login(@RequestBody LoginRequestDto user) throws  Exception {

        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(user.getEmail(),
                user.getPassword());
        Authentication auth = authenticationManager.authenticate(authReq);

        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            User authenticatedUser = userService.getUserByEmail(user.getEmail());

            if (!authenticatedUser.isActivated()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            Person person = authenticatedUser.getPerson();
            GetAccountDto accountDto = new GetAccountDto(
                    authenticatedUser.getId(),
                    authenticatedUser.getEmail(),
                    person.getName(),
                    person.getLastname(),
                    person.getPhoneNumber(),
                    person.getAddress(),
                    person.getCity().getName(),
                    authenticatedUser.getRole(),
                    jwtTokenUtil.generateToken(authenticatedUser.getEmail())
            );

            return ResponseEntity.ok(accountDto);
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
