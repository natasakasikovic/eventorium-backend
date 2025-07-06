package com.iss.eventorium.event.handlers;

import com.iss.eventorium.event.exceptions.EventTypeAlreadyExistsException;
import com.iss.eventorium.shared.models.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EventTypeExceptionHandler {

    @ExceptionHandler(EventTypeAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleEventTypeAlreadyExistsException(EventTypeAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}
