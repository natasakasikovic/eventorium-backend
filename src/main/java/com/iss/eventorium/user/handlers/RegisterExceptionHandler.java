package com.iss.eventorium.user.handlers;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.user.exceptions.ActivationTimeoutException;
import com.iss.eventorium.user.exceptions.EmailAlreadyTakenException;
import com.iss.eventorium.user.exceptions.RegistrationRequestAlreadySentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RegisterExceptionHandler {

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<ExceptionResponse> handleRegistrationExceptions(EmailAlreadyTakenException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(RegistrationRequestAlreadySentException.class)
    public ResponseEntity<ExceptionResponse> handleRegistrationRequestAlreadySentException(RegistrationRequestAlreadySentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ActivationTimeoutException.class)
    public ResponseEntity<ExceptionResponse> handleActivationTimeoutException(ActivationTimeoutException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}
