package com.iss.eventorium.event.handlers;

import com.iss.eventorium.event.exceptions.EventAlreadyPassedException;
import com.iss.eventorium.shared.models.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EventExceptionHandler {
    @ExceptionHandler(EventAlreadyPassedException.class)
    public ResponseEntity<ExceptionResponse> handleEventAlreadyPassed(EventAlreadyPassedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(e.getMessage())
                        .build());
    }
}
