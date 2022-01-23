package cz.zcu.students.cacha.bp_server.validators;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueInstitutionNameValidator implements ConstraintValidator<UniqueInstitutionName, String> {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Override
    public void initialize(UniqueInstitutionName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        Institution inDB = institutionRepository.findByName(value);
        if(inDB == null) {
            return true;
        }
        return false;
    }
}
