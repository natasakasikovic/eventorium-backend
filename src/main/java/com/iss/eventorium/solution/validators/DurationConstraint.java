package com.iss.eventorium.solution.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DurationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationConstraint {
    String message() default "Max duration should be greater than or equal to Min duration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
