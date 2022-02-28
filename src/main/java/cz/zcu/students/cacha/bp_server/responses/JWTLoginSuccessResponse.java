package cz.zcu.students.cacha.bp_server.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned on successful login
 */
@Data
@NoArgsConstructor
public class JWTLoginSuccessResponse {
    /**
     * login token
     */
    private String token;

    /**
     * Creates new instance with given login token
     * @param token login token
     */
    public JWTLoginSuccessResponse(String token) {
        this.token = token;
    }
}
