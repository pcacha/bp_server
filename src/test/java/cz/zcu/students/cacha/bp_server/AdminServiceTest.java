package cz.zcu.students.cacha.bp_server;

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
import cz.zcu.students.cacha.bp_server.view_models.UserVM;
import cz.zcu.students.cacha.bp_server.view_models.UsernameUpdateVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.ROLE_INSTITUTION_OWNER;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Set of tests to check the quality of AdminService class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AdminServiceTest {

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
    }

    /**
     * Tests getting users
     */
    @Test
    public void testGetUsers() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // call tested method
        List<UserVM> users = adminService.getUsers();
        // check right users count
        assertEquals(1, users.size());
    }

    /**
     * Tests method for getting user's detail
     */
    @Test
    public void testGetUserDetail() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // call tested method
        UserDetailVM userDetailVM = adminService.getUserDetail(user.getId());
        // check usernames are the same
        assertEquals(user.getUsername(), userDetailVM.getUsername());
    }

    /**
     * Tests updating user's username when username is valid
     */
    @Test
    public void testUpdateUserUsernameWithValidUsername() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // prepare updated username
        UsernameUpdateVM usernameUpdateVM = new UsernameUpdateVM();
        usernameUpdateVM.setUsername("updatedUsername");
        // call tested method
        adminService.updateUserUsername(usernameUpdateVM, user.getId());

        // check username is updated
        assertEquals(usernameUpdateVM.getUsername(), userRepository.getById(user.getId()).getUsername());
    }

    /**
     * Tests updating user's username when username is invalid
     */
    @Test
    public void testUpdateUserUsernameWithInvalidUsername() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);
        // add another user
        User conflictUser = testUtils.createValidUser();
        conflictUser.setUsername("conflictUsername");
        userRepository.save(conflictUser);

        // prepare updated username
        UsernameUpdateVM usernameUpdateVM = new UsernameUpdateVM();
        usernameUpdateVM.setUsername(conflictUser.getUsername());
        // call tested method and check it threw and an exception
        assertThrows(Exception.class, () -> adminService.updateUserUsername(usernameUpdateVM, user.getId()));
    }

    /**
     * Tests method for updating password
     */
    @Test
    public void testUpdateUserPassword() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // check updating password not is throwing exceptions
        assertDoesNotThrow(() -> adminService.updateUserPassword(user.getId()));
    }

    /**
     * Tests method for granting translator's rights when user is granted translator right
     */
    @Test
    public void testSetTranslatorTrue() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userRepository.save(user);

        // create boolean value
        BooleanValVM booleanValVM = new BooleanValVM();
        booleanValVM.setValue(true);

        // call tested method
        adminService.setTranslator(booleanValVM, user.getId());
        // check user is translator from db
        assertTrue(userRepository.getById(user.getId()).isTranslator());
    }

    /**
     * Tests method for granting translator's rights when user's translator right is removed
     */
    @Test
    public void testSetTranslatorFalse() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userService.save(user);

        // create boolean value
        BooleanValVM booleanValVM = new BooleanValVM();
        booleanValVM.setValue(false);

        // call tested method
        adminService.setTranslator(booleanValVM, user.getId());
        // check user is translator from db
        assertFalse(userRepository.getById(user.getId()).isTranslator());
    }

    /**
     * Tests method for removing managing of institution of a user when he is only one owner
     */
    @Test
    public void testRemoveInstitutionOneOwner() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.createValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // call test method
        adminService.removeInstitution(user.getId());
        // check system changed appropriately
        assertEquals(0, institutionRepository.count());
        assertFalse(userRepository.findById(user.getId()).get().isInstitutionOwner());
    }

    /**
     * Tests method for removing managing of institution of a user when there is more owners
     */
    @Test
    public void testRemoveInstitutionMoreOwners() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.createValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // add another manager
        User anotherManager = testUtils.createValidUser();
        anotherManager.setUsername("another manager");
        anotherManager.setInstitution(institution);
        anotherManager.getRoles().add(roleRepository.findByName(ROLE_INSTITUTION_OWNER).get());
        userRepository.save(anotherManager);

        // call test method
        adminService.removeInstitution(user.getId());
        // check system changed appropriately
        assertEquals(1, institutionRepository.count());
        assertFalse(userRepository.findById(user.getId()).get().isInstitutionOwner());
    }

    /**
     * Tests setting ban to a user
     */
    @Test
    public void testSetBanTrue() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userService.save(user);

        // prepare boolean value
        BooleanValVM booleanValVM = new BooleanValVM();
        booleanValVM.setValue(true);

        // call tested method
        adminService.setBan(booleanValVM, user.getId());
        // check user is banned
        assertTrue(userRepository.findById(user.getId()).get().getBanned());
    }

    /**
     * Tests unsetting ban to a user
     */
    @Test
    public void testSetBanFalse() {
        // prepare user in db
        User user = testUtils.createValidUser();
        userService.save(user);

        // prepare boolean value
        BooleanValVM booleanValVM = new BooleanValVM();
        booleanValVM.setValue(true);

        // ban user
        adminService.setBan(booleanValVM, user.getId());

        booleanValVM.setValue(false);
        // call tested method - unset ban
        adminService.setBan(booleanValVM, user.getId());
        // check user is not banned
        assertFalse(userRepository.findById(user.getId()).get().getBanned());
    }
}