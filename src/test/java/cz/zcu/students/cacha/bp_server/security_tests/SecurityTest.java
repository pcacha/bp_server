package cz.zcu.students.cacha.bp_server.security_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Set of tests to check security of the system
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SecurityTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * Tests if passwords are hashed in db
     */
    @Test
    public void testPasswordIsHashed() {
        // save valid user
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);

        // get user from db
        User savedUser = userRepository.findAll().get(0);
        // check password is hashed in db
        assertTrue(bCryptPasswordEncoder.matches(password, savedUser.getPassword()));
        // clear all users
        userRepository.deleteAll();
    }

    /**
     * tests security of user controller endpoints
     */
    @Test
    public void testUserEndpointsSecurity() {
        // test that all endpoints that should be secured returns unauthorized on accessing them
        ResponseEntity<Object> response1 = testRestTemplate.getForEntity("/users/token", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response1.getStatusCode());

        ResponseEntity<Object> response2 = testRestTemplate.exchange("/users/updateUser", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());

        ResponseEntity<Object> response3 = testRestTemplate.exchange("/users/updatePassword", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response3.getStatusCode());
    }

    /**
     * tests security of translation controller endpoints
     */
    @Test
    public void testTranslationEndpointsSecurity() {
        // test that all endpoints that should be secured returns unauthorized on accessing them
        ResponseEntity<Object> response1 = testRestTemplate.getForEntity("/translations/sequences", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response1.getStatusCode());

        ResponseEntity<Object> response2 = testRestTemplate.exchange("/translations/sequences/1/1", HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());

        ResponseEntity<Object> response3 = testRestTemplate.getForEntity("/translations/sequence/1/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response3.getStatusCode());

        ResponseEntity<Object> response4 = testRestTemplate.exchange("/translations/sequence/1", HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response4.getStatusCode());

        ResponseEntity<Object> response5 = testRestTemplate.getForEntity("/translations/new/1/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response5.getStatusCode());

        ResponseEntity<Object> response6 = testRestTemplate.postForEntity("/translations/new/1/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response6.getStatusCode());

        ResponseEntity<Object> response7 = testRestTemplate.getForEntity("/translations/rate/1/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response7.getStatusCode());

        ResponseEntity<Object> response8 = testRestTemplate.exchange("/translations/official/1", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response8.getStatusCode());

        ResponseEntity<Object> response9 = testRestTemplate.exchange("/translations/like/1", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response9.getStatusCode());
    }

    /**
     * tests security of institution controller endpoints
     */
    @Test
    public void testInstitutionEndpointsSecurity() {
        // test that all endpoints that should be secured returns unauthorized on accessing them
        ResponseEntity<Object> response1 = testRestTemplate.postForEntity("/institutions/myInstitution", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response1.getStatusCode());

        ResponseEntity<Object> response2 = testRestTemplate.getForEntity("/institutions/myInstitution/languages", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());

        ResponseEntity<Object> response3 = testRestTemplate.postForEntity("/institutions/myInstitution/languages/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response3.getStatusCode());

        ResponseEntity<Object> response4 = testRestTemplate.exchange("/institutions/myInstitution/updateImage", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response4.getStatusCode());

        ResponseEntity<Object> response5 = testRestTemplate.exchange("/institutions/myInstitution", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response5.getStatusCode());

        ResponseEntity<Object> response6 = testRestTemplate.getForEntity("/institutions/myInstitution", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response6.getStatusCode());

        ResponseEntity<Object> response7 = testRestTemplate.postForEntity("/institutions/myInstitution/addManager", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response7.getStatusCode());

        ResponseEntity<Object> response8 = testRestTemplate.exchange("/institutions/myInstitution", HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response8.getStatusCode());
    }

    /**
     * tests security of exhibit controller endpoints
     */
    @Test
    public void testExhibitEndpointsSecurity() {
        // test that all endpoints that should be secured returns unauthorized on accessing them
        ResponseEntity<Object> response1 = testRestTemplate.getForEntity("/exhibits/all", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response1.getStatusCode());

        ResponseEntity<Object> response2 = testRestTemplate.getForEntity("/exhibits/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());

        ResponseEntity<Object> response3 = testRestTemplate.getForEntity("/exhibits/1/qrcode", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response3.getStatusCode());

        ResponseEntity<Object> response4 = testRestTemplate.exchange("/exhibits/1", HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response4.getStatusCode());

        ResponseEntity<Object> response5 = testRestTemplate.postForEntity("/exhibits", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response5.getStatusCode());

        ResponseEntity<Object> response6 = testRestTemplate.exchange("/exhibits/1/updateImage", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response6.getStatusCode());

        ResponseEntity<Object> response7 = testRestTemplate.exchange("/exhibits/1/updateInfoLabel", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response7.getStatusCode());

        ResponseEntity<Object> response8 = testRestTemplate.exchange("/exhibits/1", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response8.getStatusCode());

        ResponseEntity<Object> response9 = testRestTemplate.getForEntity("/exhibits/approveTranslations", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response9.getStatusCode());

        ResponseEntity<Object> response10 = testRestTemplate.getForEntity("/exhibits/translate/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response10.getStatusCode());
    }

    /**
     * tests security of admin controller endpoints
     */
    @Test
    public void testAdminEndpointsSecurity() {
        // test that all endpoints that should be secured returns unauthorized on accessing them
        ResponseEntity<Object> response1 = testRestTemplate.getForEntity("/admin/users", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response1.getStatusCode());

        ResponseEntity<Object> response2 = testRestTemplate.getForEntity("/admin/users/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());

        ResponseEntity<Object> response3 = testRestTemplate.exchange("/admin/users/1/updateUsername", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response3.getStatusCode());

        ResponseEntity<Object> response4 = testRestTemplate.exchange("/admin/users/1/updatePassword", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response4.getStatusCode());

        ResponseEntity<Object> response5 = testRestTemplate.exchange("/admin/users/1/updateTranslator", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response5.getStatusCode());

        ResponseEntity<Object> response6 = testRestTemplate.exchange("/admin/users/1/removeInstitution", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response6.getStatusCode());

        ResponseEntity<Object> response7 = testRestTemplate.exchange("/admin/users/1/updateBan", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response7.getStatusCode());
    }

    /**
     * tests security of location controller endpoints
     */
    @Test
    public void testLocationEndpointsSecurity() {
        // test that all endpoints that should be secured returns unauthorized on accessing them
        ResponseEntity<Object> response1 = testRestTemplate.getForEntity("/location/buildings", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response1.getStatusCode());

        ResponseEntity<Object> response2 = testRestTemplate.getForEntity("/location/buildings/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());

        ResponseEntity<Object> response3 = testRestTemplate.postForEntity("/location/buildings", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response3.getStatusCode());

        ResponseEntity<Object> response4 = testRestTemplate.exchange("/location/buildings/1", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response4.getStatusCode());

        ResponseEntity<Object> response5 = testRestTemplate.exchange("/location/buildings/1", HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response5.getStatusCode());

        ResponseEntity<Object> response6 = testRestTemplate.getForEntity("/location/rooms/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response6.getStatusCode());

        ResponseEntity<Object> response7 = testRestTemplate.postForEntity("/location/rooms/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response7.getStatusCode());

        ResponseEntity<Object> response8 = testRestTemplate.exchange("/location/rooms/1", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response8.getStatusCode());

        ResponseEntity<Object> response9 = testRestTemplate.exchange("/location/rooms/1", HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response9.getStatusCode());

        ResponseEntity<Object> response10 = testRestTemplate.getForEntity("/location/showcases/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response10.getStatusCode());

        ResponseEntity<Object> response11 = testRestTemplate.postForEntity("/location/showcases/1", null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response11.getStatusCode());

        ResponseEntity<Object> response12 = testRestTemplate.exchange("/location/showcases/1", HttpMethod.PUT, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response12.getStatusCode());

        ResponseEntity<Object> response13 = testRestTemplate.exchange("/location/showcases/1", HttpMethod.DELETE, null, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response13.getStatusCode());
    }
}
