package com.iss.eventorium.user.handlers;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.user.exceptions.AccountDeactivationNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccountDeactivationExceptionHandler {

    @ExceptionHandler(AccountDeactivationNotAllowedException.class)
    public ResponseEntity<ExceptionResponse> handleAccountDeactivationNotAllowedHandler(AccountDeactivationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}
