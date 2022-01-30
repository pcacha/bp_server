package cz.zcu.students.cacha.bp_server.validators;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UniqueInstitutionNameExclUpdatedValidator implements ConstraintValidator<UniqueInstitutionNameExclUpdated, String> {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Override
    public void initialize(UniqueInstitutionNameExclUpdated constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Institution> institutionOptional = institutionRepository.findByName(value);

        if(institutionOptional.isEmpty()) {
            return true;
        }

        if(auth.getInstitution() != null && auth.getInstitution().equals(institutionOptional.get())) {
            return true;
        }
        return false;
    }
}
