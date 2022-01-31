package cz.zcu.students.cacha.bp_server.responses;

import lombok.Data;

@Data
public class JWTLoginSuccessResponse {
    private String token;

    public JWTLoginSuccessResponse(String token) {
        this.token = token;
    }
}
