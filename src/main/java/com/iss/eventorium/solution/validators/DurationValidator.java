package com.iss.eventorium.solution.validators;

import com.iss.eventorium.solution.dtos.services.CreateServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.UpdateServiceRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DurationValidator implements ConstraintValidator<DurationConstraint, Object> {

    @Override
    public void initialize(DurationConstraint constraintAnnotation) {
        // Should not do anything
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof CreateServiceRequestDto dto) {
            return isValidDuration(dto.getMinDuration(), dto.getMaxDuration());
        } else if (value instanceof UpdateServiceRequestDto dto) {
            return isValidDuration(dto.getMinDuration(), dto.getMaxDuration());
        }
        return true;
    }

    private boolean isValidDuration(Integer minDuration, Integer maxDuration) {
        if (maxDuration != null && minDuration != null) {
            return maxDuration >= minDuration;
        }
        return true;
    }
}
