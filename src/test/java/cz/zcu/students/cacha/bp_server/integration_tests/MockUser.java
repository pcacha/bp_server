package cz.zcu.students.cacha.bp_server.integration_tests;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User class for testing purposes
 */
@Data
@NoArgsConstructor
public class MockUser {

    /**
     * username
     */
    private String username;
    /**
     * password
     */
    private String password;
    /**
     * email
     */
    private String email;

    /**
     * Creates new instance with given parameters
     * @param username username
     * @param password passsword
     * @param email email
     */
    public MockUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
