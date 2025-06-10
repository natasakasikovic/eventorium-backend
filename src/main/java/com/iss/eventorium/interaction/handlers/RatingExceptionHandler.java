package com.iss.eventorium.interaction.handlers;

import com.iss.eventorium.interaction.exceptions.AlreadyRatedException;
import com.iss.eventorium.shared.models.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RatingExceptionHandler {

    @ExceptionHandler(AlreadyRatedException.class)
    public ResponseEntity<ExceptionResponse> handleAlreadyRatedException(AlreadyRatedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(e.getMessage())
                        .build());
    }
}
