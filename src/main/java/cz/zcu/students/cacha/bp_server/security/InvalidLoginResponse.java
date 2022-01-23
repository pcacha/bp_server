package cz.zcu.students.cacha.bp_server.security;

import lombok.Data;

@Data
public class InvalidLoginResponse {
    private String username;
    private String password;

    public InvalidLoginResponse() {
        this.username = "Invalid name";
        this.password = "Invalid password";
    }
}
