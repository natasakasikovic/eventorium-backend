package com.iss.eventorium.category.handlers;

import com.iss.eventorium.category.exceptions.CategoryAlreadyExistsException;
import com.iss.eventorium.category.exceptions.CategoryInUseException;
import com.iss.eventorium.shared.models.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CategoryExceptionHandler {

    @ExceptionHandler({ CategoryInUseException.class, CategoryAlreadyExistsException.class })
    public ResponseEntity<ExceptionResponse> handleCategoryException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}
