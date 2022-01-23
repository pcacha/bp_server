package cz.zcu.students.cacha.bp_server.view_models;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UsernamePasswordVM {
    @NotNull(message = "Username can not be blank")
    private String username;
    @NotNull(message = "Password can not be blank")
    private String password;
}
