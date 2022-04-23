package cz.zcu.students.cacha.bp_server.validators;

import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

/**
 * Class used to check email uniqueness except the email of user that caused the check
 */
public class UniqueEmailExclPrincipalValidator implements ConstraintValidator<UniqueEmailExclPrincipal, String> {

    @Autowired
    private UserRepository userRepository;

    /**
     * Initializes validator
     * @param constraintAnnotation annotation
     */
    @Override
    public void initialize(UniqueEmailExclPrincipal constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Gets if email is unique except email of the user that caused the check
     * @param value email
     * @param constraintValidatorContext validator context
     * @return if email is unique except email of the user that caused the check
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        Optional<User> inDBOptional = userRepository.findByEmail(value);

        // if user with given email not found it is ok
        if(inDBOptional.isEmpty()) {
            return true;
        }

        User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(inDBOptional.get().getId().equals(auth.getId())) {
            // if the email belong to the user that caused the check it is ok
            return true;
        }
        return false;
    }
}
