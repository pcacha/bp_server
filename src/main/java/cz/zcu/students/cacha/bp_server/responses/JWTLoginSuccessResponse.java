package cz.zcu.students.cacha.bp_server.responses;

import lombok.Data;

@Data
public class JWTLoginSuccessResponse {
    private boolean success;
    private String token;

    public JWTLoginSuccessResponse(boolean success, String token) {
        this.success = success;
        this.token = token;
    }
}
