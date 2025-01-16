package com.iss.eventorium.shared.handlers;

import com.iss.eventorium.category.exceptions.CategoryAlreadyExistsException;
import com.iss.eventorium.category.exceptions.CategoryInUseException;
import com.iss.eventorium.shared.exceptions.AlreadyInFavoritesException;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.exceptions.ImageUploadException;
import com.iss.eventorium.shared.utils.ExceptionResponse;
import com.iss.eventorium.solution.exceptions.ServiceAlreadyReservedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : ex.getBindingResult().getGlobalError() != null
                ? ex.getBindingResult().getGlobalError().getDefaultMessage()
                : "Validation error occurred";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(errorMessage)
                        .build());
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleImageNotFoundException(ImageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<ExceptionResponse> handleCategoryInUseException(CategoryInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(AlreadyInFavoritesException.class)
    public ResponseEntity<ExceptionResponse> handleImageNotFoundException(AlreadyInFavoritesException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file upload: " + ex.getMessage());
    }

    @ExceptionHandler(ServiceAlreadyReservedException.class)
    public ResponseEntity<ExceptionResponse> handleServiceAlreadyReserved(ServiceAlreadyReservedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error("Service Already Reserved")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error("Category already exists!")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ExceptionResponse> handleImageUploadException(ImageUploadException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .error(ex.getMessage())
                        .message(ex.getMessage())
                        .build());
    }
}
