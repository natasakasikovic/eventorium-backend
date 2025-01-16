package com.iss.eventorium.solution.handlers;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.solution.exceptions.ServiceAlreadyReservedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(ServiceAlreadyReservedException.class)
    public ResponseEntity<ExceptionResponse> handleServiceAlreadyReserved(ServiceAlreadyReservedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

}
