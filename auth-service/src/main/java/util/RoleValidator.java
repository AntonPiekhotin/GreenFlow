package util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class RoleValidator implements ConstraintValidator<ValidRole, String> {

    private static final List<String> VALID_ROLES = List.of("CLIENT", "WORKER");

    @Override
    public boolean isValid(String role, ConstraintValidatorContext constraintValidatorContext) {
        if (role == null) {
            return false;
        }
        return VALID_ROLES.contains(role.toUpperCase());
    }
}
