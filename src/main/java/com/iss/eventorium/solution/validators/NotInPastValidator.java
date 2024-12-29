package com.iss.eventorium.solution.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class NotInPastValidator implements ConstraintValidator<NotInPast, LocalDate> {

    @Override
    public void initialize(NotInPast constraintAnnotation) {}

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(LocalDate.now());
    }
}
