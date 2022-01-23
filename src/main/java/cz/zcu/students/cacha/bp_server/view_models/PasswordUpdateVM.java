package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PasswordUpdateVM {
    @Size(min = 8, max = 255, message = "Password must be between 8 to 255 letters long")
    @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="Password must contain a lowercase and an uppercase letter and a number")
    private String password;
}
