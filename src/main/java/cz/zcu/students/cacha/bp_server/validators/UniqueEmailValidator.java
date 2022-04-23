package cz.zcu.students.cacha.bp_server.validators;

import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserRepository userRepository;

    /**
     * Initializes validator
     * @param constraintAnnotation annotation
     */
    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Gets if email is unique
     * @param value email
     * @param constraintValidatorContext validator context
     * @return if email is unique
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        Optional<User> inDBOptional = userRepository.findByEmail(value);
        if(inDBOptional.isEmpty()) {
            return true;
        }
        return false;
    }
}
