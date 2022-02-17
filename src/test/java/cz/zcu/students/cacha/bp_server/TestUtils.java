package cz.zcu.students.cacha.bp_server;

import cz.zcu.students.cacha.bp_server.domain.User;

/**
 * Utility class for supporting tests
 */
public class TestUtils {

    /**
     * Creates valid system user
     * @return valid user
     */
    public static User createValidUser() {
        User user = new User();
        // set valid properties
        user.setUsername("test-user");
        user.setEmail("test-user@email.com");
        user.setPassword("P4ssword");
        return user;
    }
}
