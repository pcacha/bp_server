package cz.zcu.students.cacha.bp_server.integration_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import cz.zcu.students.cacha.bp_server.repositories.LanguageRepository;
import cz.zcu.students.cacha.bp_server.repositories.RoleRepository;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.services.UserService;
import cz.zcu.students.cacha.bp_server.view_models.InstitutionVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Set of tests to check the quality of InstitutionController endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class InstitutionControllerIntegrationTest {
    /**
     * folder of institutions images
     */
    @Value("${cts.paths.institutions_images_folder}")
    private String INSTITUTIONS_IMAGES_FOLDER;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * called after each test
     * provides cleanup of db and images
     */
    @AfterEach
    public void cleanup() {
        // delete data from db
        userRepository.deleteAll();
        institutionRepository.deleteAll();

        // delete all images from img folders
        testUtils.clearImages();
        // clear headers
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    /**
     * tests get institutions method
     */
    @Test
    public void testGetInstitutions() {
        // save user
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);
        // prepare institution
        Institution institution = testUtils.createValidInstitution();
        institutionRepository.save(institution);

        // call tested endpoint
        ResponseEntity<List> response = testRestTemplate.getForEntity("/institutions", List.class);
        // check institutions count
        assertEquals(1, response.getBody().size());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * tests save institution endpoint
     */
    @Test
    public void testSaveInstitution() {
        // delete images
        testUtils.deleteFolderContent(INSTITUTIONS_IMAGES_FOLDER);
        // prepare institution
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        MockInstitution institution = new MockInstitution("test name", "test address", "49.750218", "13.378601");

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<GenericResponse> response = testRestTemplate.postForEntity("/institutions/myInstitution", institution, GenericResponse.class);
        // check expected counts
        assertEquals(1, institutionRepository.count());
        assertEquals(0, testUtils.getFolderContentCount(INSTITUTIONS_IMAGES_FOLDER));
        // check status
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    /**
     * tests add language endpoint
     */
    @Test
    public void testAddLanguage() {
        // prepare institution and user
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        Institution institution = testUtils.createValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        Long id = languageRepository.findByCode("cs").get().getId();
        ResponseEntity<GenericResponse> response = testRestTemplate.postForEntity("/institutions/myInstitution/languages/" + id, null, GenericResponse.class);

        // check count
        assertEquals(1, institutionRepository.findById(institution.getId()).get().getLanguages().size());
        // check status
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * tests get my institution endpoint
     */
    @Test
    public void testGetMyInstitution() {
        // prepare institution
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        // save institution
        Institution institution = testUtils.createValidInstitution();
        institutionService.saveInstitution(institution, user);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<InstitutionVM> response = testRestTemplate.getForEntity("/institutions/myInstitution", InstitutionVM.class);

        // check tested method is not returning null
        assertNotNull(response.getBody());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * tests delete my institution endpoint
     */
    @Test
    public void testDeleteMyInstitution() {
        // prepare institution and user
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        Institution institution = testUtils.createValidInstitution();

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // save institution
        institutionService.saveInstitution(institution, user);

        // call tested endpoint
        testRestTemplate.delete("/institutions/myInstitution");
        // check institutions count
        assertEquals(0, institutionRepository.count());
    }
}
