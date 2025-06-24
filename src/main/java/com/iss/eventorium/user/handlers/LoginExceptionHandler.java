package com.iss.eventorium.user.handlers;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.user.exceptions.AccountAccessDeniedException;
import com.iss.eventorium.user.exceptions.UserSuspendedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LoginExceptionHandler {

    @ExceptionHandler(UserSuspendedException.class)
    public ResponseEntity<ExceptionResponse> handleUserSuspendedException(UserSuspendedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ExceptionResponse> handleAuthenticationExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message("Invalid email or password.")
                        .build());
    }

    @ExceptionHandler(AccountAccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccountAccessDeniedException(AccountAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}