package cz.zcu.students.cacha.bp_server.view_models;

import cz.zcu.students.cacha.bp_server.validators.UniqueUsernameExclPrincipal;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UsernameUpdateVM {
    @NotNull(message = "Username can not be blank")
    @Size(min = 3, max = 255, message = "Username must be between 3 to 255 letters long")
    @UniqueUsernameExclPrincipal
    private String username;
}
