package com.iss.eventorium.shared.validators.password;

import com.iss.eventorium.user.dtos.QuickRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, QuickRegistrationRequestDto> {

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) { }

    @Override
    public boolean isValid(QuickRegistrationRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getPasswordConfirmation() == null) {
            return false;
        }
        return dto.getPassword().equals(dto.getPasswordConfirmation());
    }
}