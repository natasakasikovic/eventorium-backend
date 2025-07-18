package com.iss.eventorium.event.handlers;

import com.iss.eventorium.event.exceptions.AlreadyProcessedException;
import com.iss.eventorium.event.exceptions.ProductNotAvailableException;
import com.iss.eventorium.shared.models.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BudgetExceptionHandler {

    @ExceptionHandler(AlreadyProcessedException.class)
    public ResponseEntity<ExceptionResponse> handleAlreadyPurchasedException(AlreadyProcessedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<ExceptionResponse> handleProductNotAvailableException(ProductNotAvailableException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}
