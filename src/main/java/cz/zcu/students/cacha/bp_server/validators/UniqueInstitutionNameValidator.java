package cz.zcu.students.cacha.bp_server.validators;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UniqueInstitutionNameValidator implements ConstraintValidator<UniqueInstitutionName, String> {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Override
    public void initialize(UniqueInstitutionName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Institution> inDBOptional = institutionRepository.findByName(value);
        if(inDBOptional.isEmpty()) {
            return true;
        }
        return false;
    }
}
