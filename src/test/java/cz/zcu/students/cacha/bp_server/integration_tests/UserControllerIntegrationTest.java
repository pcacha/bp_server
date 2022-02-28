package cz.zcu.students.cacha.bp_server.integration_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.responses.JWTLoginSuccessResponse;
import cz.zcu.students.cacha.bp_server.services.UserService;
import cz.zcu.students.cacha.bp_server.view_models.UsernamePasswordVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Set of tests to check the quality of UserController endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    TestRestTemplate testRestTemplate;

    /**
     * called after each test
     * provides cleanup of db
     */
    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    /**
     * Tests registering of new user
     */
    @Test
    public void testRegister() {
        // create test user
        MockUser user = new MockUser("pavel", "P4ssword", "abc@abc.com");

        // call endpoint
        ResponseEntity<Object> registerResponse = testRestTemplate.postForEntity("/users/register", user, Object.class);
        // check status code and user in db
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertThat(userRepository.count()).isEqualTo(1);
    }

    /**
     * Tests logging in
     */
    @Test
    public void testLogin() {
        // create valid user
        User user = testUtils.createValidUser();
        // prepare logging in VM
        UsernamePasswordVM usernamePasswordVM = new UsernamePasswordVM();
        usernamePasswordVM.setUsername(user.getUsername());
        usernamePasswordVM.setPassword(user.getPassword());
        // save user
        userService.save(user);

        // call endpoint
        ResponseEntity<JWTLoginSuccessResponse> loginResponse = testRestTemplate.postForEntity("/users/login", usernamePasswordVM, JWTLoginSuccessResponse.class);
        // check OK response and response body
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody().getToken());
    }

    /**
     * Tests getting fresh token
     */
    @Test
    public void testToken() {
        // save valid user
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        // authenticate user
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<JWTLoginSuccessResponse> tokenResponse = testRestTemplate.getForEntity("/users/token", JWTLoginSuccessResponse.class);
        // check OK response and response body
        assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());
        assertNotNull(tokenResponse.getBody().getToken());
    }
}
