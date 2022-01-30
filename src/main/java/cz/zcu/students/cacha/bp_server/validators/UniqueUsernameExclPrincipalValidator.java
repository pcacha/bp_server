package cz.zcu.students.cacha.bp_server.validators;

import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UniqueUsernameExclPrincipalValidator implements ConstraintValidator<UniqueUsernameExclPrincipal, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UniqueUsernameExclPrincipal constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        Optional<User> inDBOptional = userRepository.findByUsername(value);

        if(inDBOptional.isEmpty()) {
            return true;
        }

        User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(inDBOptional.get().equals(auth)) {
            return true;
        }
        return false;
    }
}
