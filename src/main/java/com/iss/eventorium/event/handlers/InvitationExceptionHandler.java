package com.iss.eventorium.event.handlers;

import com.iss.eventorium.event.exceptions.InvitationLimitExceededException;
import com.iss.eventorium.shared.models.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvitationExceptionHandler {
    @ExceptionHandler(InvitationLimitExceededException.class)
    public ResponseEntity<ExceptionResponse> handleInvitationLimitExceeded(InvitationLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(e.getMessage())
                        .build());
    }
}