package cz.zcu.students.cacha.bp_server.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to check uniqueness of email except email of user that caused the check
 */
@Constraint(validatedBy = UniqueEmailExclPrincipalValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmailExclPrincipal {
    String message() default "E-mail is already taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
