package cz.zcu.students.cacha.bp_server.unit_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.services.UserService;
import cz.zcu.students.cacha.bp_server.view_models.UsernamePasswordVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Set of tests to check the quality of UserService class
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private TestUtils testUtils;

    /**
     * called after each test
     * provides cleanup of db
     */
    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    /**
     * Tests if new user is successfully saved into db
     */
    @Test
    public void testSaveUser() {
        saveValidUser();
        // check if user is in the db
        assertThat(userRepository.count()).isEqualTo(1);
    }

    /**
     * Saves valid user into db
     * @return saved user
     */
    public User saveValidUser() {
        User user = testUtils.createValidUser();
        userService.save(user);
        return user;
    }

    /**
     * Tests successful login
     */
    @Test
    public void testLoginSuccess() {
        User user = saveValidUser();
        UsernamePasswordVM usernamePasswordVM = getLoginVM();
        // check exception was not thrown
        assertDoesNotThrow(() -> userService.login(usernamePasswordVM));
    }

    /**
     * Tests unsuccessful login
     */
    @Test
    public void testLoginFail() {
        User user = saveValidUser();
        UsernamePasswordVM usernamePasswordVM = getLoginVM();
        // change password to invalid password
        usernamePasswordVM.setPassword("invalid");
        // check exception was thrown
        assertThrows(Exception.class, () -> userService.login(usernamePasswordVM));
    }

    /**
     * Get loginVM of given user
     * @return loginVM
     */
    public UsernamePasswordVM getLoginVM() {
        // create usernamePasswordVM and fill data
        UsernamePasswordVM usernamePasswordVM = new UsernamePasswordVM();
        User user = testUtils.createValidUser();
        usernamePasswordVM.setUsername(user.getUsername());
        usernamePasswordVM.setPassword(user.getPassword());
        return usernamePasswordVM;
    }

    /**
     * Tests that fresh token is generated
     */
    @Test
    public void testGetsFreshToken() {
        saveValidUser();
        UsernamePasswordVM usernamePasswordVM = getLoginVM();
        // login user into system
        userService.login(usernamePasswordVM);
        // try get to get new token
        assertDoesNotThrow(() -> userService.getFreshToken());
    }

    /**
     * Tests user information update
     */
    @Test
    public void updateUserTest() {
        // save user
        User user = saveValidUser();
        // update user
        String newUsername = "newName";
        String newEmail = "newEmail";
        userService.updateUser(user, newUsername, newEmail);

        // check if user was updated
        User updated = userRepository.findAll().get(0);
        assertEquals(newUsername, updated.getUsername());
        assertEquals(newEmail, updated.getEmail());
    }

    /**
     * Tests user password update
     */
    @Test
    public void testUpdatePassword() {
        // save user
        User user = saveValidUser();
        // update password
        String newPassword = "newPassword";
        userService.updatePassword(user, newPassword);

        // check if password was updated
        User updated = userRepository.findAll().get(0);
        assertTrue(bCryptPasswordEncoder.matches(newPassword, updated.getPassword()));
    }
}
