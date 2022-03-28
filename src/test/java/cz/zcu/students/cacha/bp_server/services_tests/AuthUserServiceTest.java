package cz.zcu.students.cacha.bp_server.services_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.services.AuthUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Set of tests to check the quality of AuthUserService class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthUserServiceTest {

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils testUtils;

    /**
     * called after each test
     *
     */
    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    /**
     * Tests method for getting user based on his username
     */
    @Test
    public void testLoadUserByUsername() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // call tested method
        UserDetails userDetails = authUserService.loadUserByUsername(user.getUsername());
        // check the ids are the same
        assertEquals(user.getId(), ((User) userDetails).getId());
    }

    /**
     * Tests method for getting user based on his id
     */
    @Test
    public void testLoadUserById() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // call tested method
        UserDetails userDetails = authUserService.loadUserById(user.getId());
        // check the usernames are the same
        assertEquals(user.getUsername(), ((User) userDetails).getUsername());
    }
}
