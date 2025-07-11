package com.iss.eventorium.solution.handlers;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.solution.exceptions.*;
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

    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<ExceptionResponse> handleReservationConflict(ReservationConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidServiceDurationException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidServiceDuration(InvalidServiceDurationException ex) {
     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
             .body(ExceptionResponse.builder()
                     .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                     .message(ex.getMessage())
                     .build());
    }

    @ExceptionHandler(ReservationDeadlineExceededException.class)
    public ResponseEntity<ExceptionResponse> handleReservationDeadlineExceeded(ReservationDeadlineExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ReservationOutsideWorkingHoursException.class)
    public ResponseEntity<ExceptionResponse> handleReservationOutsideWorkingHours(ReservationOutsideWorkingHoursException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ReservationForPastEventException.class)
    public ResponseEntity<ExceptionResponse> handleReservationForPastEvent(ReservationForPastEventException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ServiceNotAvailableException.class)
    public ResponseEntity<ExceptionResponse> handleServiceNotAvailable(ServiceNotAvailableException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}