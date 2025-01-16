package com.iss.eventorium.user.handlers;

import com.iss.eventorium.shared.utils.ExceptionResponse;
import com.iss.eventorium.user.exceptions.InvalidOldPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserUpdateExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleInvalidOldPasswordException(InvalidOldPasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}
