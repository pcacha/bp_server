package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * view model for passing password
 */
@Data
public class PasswordUpdateVM {

    /**
     * password
     */
    @NotNull(message = "Password can not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="Password must contain a lowercase and an uppercase letter and a number")
    private String password;
}
