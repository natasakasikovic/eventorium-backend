package com.iss.eventorium.user.validators.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {
    String passwordField;
    String passwordConfirmationField;

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
        passwordField = constraintAnnotation.password();
        passwordConfirmationField = constraintAnnotation.passwordConfirmation();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object password = new BeanWrapperImpl(value).getPropertyValue(passwordField);
        Object passwordConfirmation = new BeanWrapperImpl(value).getPropertyValue(passwordConfirmationField);
        return password != null && password.equals(passwordConfirmation);
    }
}