package com.iss.eventorium.user.handlers;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.user.exceptions.SelfBlockNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserBlockHandler {
    @ExceptionHandler(SelfBlockNotAllowedException.class)
    public ResponseEntity<ExceptionResponse> handleSelfBlockNotAllowedException(SelfBlockNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}