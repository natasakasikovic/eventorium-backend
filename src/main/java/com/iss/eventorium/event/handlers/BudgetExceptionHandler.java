package com.iss.eventorium.event.handlers;

import com.iss.eventorium.event.exceptions.AlreadyPurchasedException;
import com.iss.eventorium.shared.exceptions.InsufficientFundsException;
import com.iss.eventorium.shared.models.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BudgetExceptionHandler {

    @ExceptionHandler(AlreadyPurchasedException.class)
    public ResponseEntity<ExceptionResponse> handleAlreadyPurchasedException(AlreadyPurchasedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}
