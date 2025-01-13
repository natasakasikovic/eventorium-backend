package com.iss.eventorium.user.handlers;

import com.iss.eventorium.shared.utils.ExceptionResponse;
import com.iss.eventorium.user.exceptions.UserSuspendedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}