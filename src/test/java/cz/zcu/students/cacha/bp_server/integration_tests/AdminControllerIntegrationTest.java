package cz.zcu.students.cacha.bp_server.integration_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import cz.zcu.students.cacha.bp_server.repositories.RoleRepository;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.services.AdminService;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.services.UserService;
import cz.zcu.students.cacha.bp_server.view_models.BooleanValVM;
import cz.zcu.students.cacha.bp_server.view_models.UserDetailVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.ROLE_ADMIN;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Set of tests to check the quality of AdminService endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AdminControllerIntegrationTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * called after each method
     * provides cleanup of data
     */
    @AfterEach
    public void cleanup() {
        // delete all users
        userRepository.deleteAll();
        institutionRepository.deleteAll();

        // delete images of institutions
        testUtils.clearImages();
        // clear headers
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    /**
     * Tests endpoint for getting users
     */
    @Test
    public void testGetUsers() {
        // prepare admin
        User userAdmin = createAdmin();
        // authenticate
        testUtils.authenticate(userAdmin, testUtils.createValidUser().getPassword(), testRestTemplate);

        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // call tested endpoint
        ResponseEntity<List> response = testRestTemplate.getForEntity("/admin/users", List.class);
        // check right users count
        assertEquals(1, response.getBody().size());
        // check status
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * creates valid admin
     * @return valid admin
     */
    private User createAdmin() {
        // create user
        User userAdmin = testUtils.createValidUser();
        userAdmin.setUsername("admin");
        // add admin rights
        userAdmin.getRoles().add(roleRepository.findByName(ROLE_ADMIN).get());
        // save admin
        userService.save(userAdmin);
        return userAdmin;
    }

    /**
     * Tests endpoint for getting user's detail
     */
    @Test
    public void testGetUserDetail() {
        // prepare admin
        User userAdmin = createAdmin();
        // authenticate
        testUtils.authenticate(userAdmin, testUtils.createValidUser().getPassword(), testRestTemplate);

        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // call tested endpoint
        ResponseEntity<UserDetailVM> response = testRestTemplate.getForEntity("/admin/users/" + user.getId(), UserDetailVM.class);
        // check usernames are the same
        assertEquals(user.getUsername(), response.getBody().getUsername());
        // check status
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Tests endpoint for granting translator's rights when user is granted translator right
     */
    @Test
    public void testSetTranslatorTrue() {
        // prepare admin
        User userAdmin = createAdmin();
        // authenticate
        testUtils.authenticate(userAdmin, testUtils.createValidUser().getPassword(), testRestTemplate);

        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // create boolean value
        BooleanValVM booleanValVM = new BooleanValVM();
        booleanValVM.setValue(true);

        // call tested endpoint
        testRestTemplate.put("/admin/users/" + user.getId() + "/updateTranslator", booleanValVM, Object.class);
        // check user is translator from db
        assertTrue(userRepository.getById(user.getId()).isTranslator());
    }

    /**
     * Tests endpoint for granting translator's rights when user's translator right is removed
     */
    @Test
    public void testSetTranslatorFalse() {
        // prepare admin
        User userAdmin = createAdmin();
        // authenticate
        testUtils.authenticate(userAdmin, testUtils.createValidUser().getPassword(), testRestTemplate);

        // prepare user in db
        User user = testUtils.createValidUser();
        userService.save(user);

        // create boolean value
        BooleanValVM booleanValVM = new BooleanValVM();
        booleanValVM.setValue(false);

        // call tested endpoint
        testRestTemplate.put("/admin/users/" + user.getId() + "/updateTranslator", booleanValVM, Object.class);
        // check user is translator from db
        assertFalse(userRepository.getById(user.getId()).isTranslator());
    }

    /**
     * Tests endpoint for removing managing of institution of a user when he is only one owner
     */
    @Test
    public void testRemoveInstitutionOneOwner() {
        // prepare admin
        User userAdmin = createAdmin();
        // authenticate
        testUtils.authenticate(userAdmin, testUtils.createValidUser().getPassword(), testRestTemplate);

        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.createValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // call test endpoint
        testRestTemplate.put("/admin/users/" + user.getId() + "/removeInstitution", Object.class);
        // check system changed appropriately
        assertEquals(0, institutionRepository.count());
        assertFalse(userRepository.findById(user.getId()).get().isInstitutionOwner());
    }

    /**
     * Tests endpoint for setting ban to a user
     */
    @Test
    public void testSetBanTrue() {
        // prepare admin
        User userAdmin = createAdmin();
        // authenticate
        testUtils.authenticate(userAdmin, testUtils.createValidUser().getPassword(), testRestTemplate);

        // prepare user in db
        User user = testUtils.createValidUser();
        userService.save(user);

        // prepare boolean value
        BooleanValVM booleanValVM = new BooleanValVM();
        booleanValVM.setValue(true);

        // call tested endpoint
        testRestTemplate.put("/admin/users/" + user.getId() + "/updateBan", booleanValVM, Object.class);
        adminService.setBan(booleanValVM, user.getId());
        // check user is banned
        assertTrue(userRepository.findById(user.getId()).get().getBanned());
    }

    /**
     * Tests endpoint for unsetting ban to a user
     */
    @Test
    public void testSetBanFalse() {
        // prepare admin
        User userAdmin = createAdmin();
        // authenticate
        testUtils.authenticate(userAdmin, testUtils.createValidUser().getPassword(), testRestTemplate);

        // prepare user in db
        User user = testUtils.createValidUser();
        userService.save(user);

        // prepare boolean value
        BooleanValVM booleanValVM = new BooleanValVM();
        booleanValVM.setValue(true);

        // ban user
        adminService.setBan(booleanValVM, user.getId());

        booleanValVM.setValue(false);
        // call tested endpoint
        testRestTemplate.put("/admin/users/" + user.getId() + "/updateBan", booleanValVM, Object.class);
        // check user is not banned
        assertFalse(userRepository.findById(user.getId()).get().getBanned());
    }
}
