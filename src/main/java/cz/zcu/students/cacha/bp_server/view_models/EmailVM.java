package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.validators.UniqueEmail;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * view model for email
 */
@Data
@NoArgsConstructor
public class EmailVM {

    /**
     * email
     */
    @NotNull(message = "E-mail can not be blank")
    @Email(regexp = ".+@.+\\..+", message = "Bad e-mail format")
    @Size(min = 1, max = 50, message = "E-mail must be maximally 50 letters long")
    @UniqueEmail
    private String email;
}
