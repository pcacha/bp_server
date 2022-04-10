package cz.zcu.students.cacha.bp_server.integration_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.ExhibitRepository;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import cz.zcu.students.cacha.bp_server.repositories.TranslationRepository;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.ExhibitService;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.services.UserService;
import cz.zcu.students.cacha.bp_server.view_models.ExhibitVM;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Set of tests to check the quality of ExhibitController endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ExhibitControllerIntegrationTest {

    /**
     * folder of exhibits images
     */
    @Value("${cts.paths.exhibits_images_folder}")
    private String EXHIBITS_IMAGES_FOLDER;
    /**
     * folder of info label images
     */
    @Value("${cts.paths.info_labels_images_folder}")
    private String INFO_LABELS_IMAGES_FOLDER;

    @Autowired
    private ExhibitService exhibitService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private ExhibitRepository exhibitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserService userService;

    /**
     * called after each test
     * provides cleanup of db and images
     */
    @AfterEach
    public void cleanup() {
        // delete data from db
        translationRepository.deleteAll();
        userRepository.deleteAll();
        exhibitRepository.deleteAll();
        institutionRepository.deleteAll();

        // delete all images from img folders
        testUtils.clearImages();
        // clear headers
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    /**
     * Saves exhibit with its institution and manager to db
     * @param user user
     * @param exhibit exhibit
     */
    private void saveExhibit(User user, Exhibit exhibit) {
        // save institution and manager
        institutionService.saveInstitution(exhibit.getInstitution(), user);
        // save exhibit
        exhibitService.saveExhibit(exhibit, user);
    }

    /**
     * Tests get exhibits of institution endpoint
     */
    @Test
    public void testGetExhibitsOfInstitution() {
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save institution with manager and exhibits
        saveExhibit(user, exhibit);

        // call tested endpoint
        ResponseEntity<List> response = testRestTemplate.getForEntity("/exhibits/all/" + exhibit.getInstitution().getId(), List.class);
        // check right count
        assertEquals(1, response.getBody().size());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Tests endpoint for getting all exhibits of user institution
     */
    @Test
    public void testGetAllExhibitsOfUserInstitution() {
        // prepare institution with exhibits and user
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<List> response = testRestTemplate.getForEntity("/exhibits/all/", List.class);
        // check right result count
        assertEquals(1, response.getBody().size());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Tests method for getting exhibit
     */
    @Test
    public void testGetExhibit() {
        // prepare institution with exhibits and user
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<ExhibitVM> response = testRestTemplate.getForEntity("/exhibits/" + exhibit.getId(), ExhibitVM.class);
        // check result
        assertNotNull(response.getBody());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Tests endpoint providing qr code of an exhibit
     */
    @Test
    public void testGetExhibitQRCode() {
        // prepare institution with exhibits and user
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<String> response = testRestTemplate.getForEntity("/exhibits/" + exhibit.getId() + "/qrcode", String.class);
        // check response is not null
        assertNotNull(response.getBody());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Tests delete exhibit endpoint
     */
    @Test
    public void testDeleteExhibit() {
        // delete images
        testUtils.deleteFolderContent(EXHIBITS_IMAGES_FOLDER);
        testUtils.deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        Exhibit exhibit = testUtils.createValidExhibit();
        // add image
        exhibit.setEncodedImage(testUtils.getEncodedImage());
        // save institution with manager and exhibits
        saveExhibit(user, exhibit);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        testRestTemplate.delete("/exhibits/" + exhibit.getId());
        // check there is now no exhibit in db
        assertEquals(0, exhibitRepository.count());
        // check there is no image stored
        assertEquals(0, testUtils.getFolderContentCount(EXHIBITS_IMAGES_FOLDER));
        assertEquals(0, testUtils.getFolderContentCount(INFO_LABELS_IMAGES_FOLDER));
    }

    /**
     * Tests endpoint for saving exhibit to user institution
     */
    @Test
    public void testSaveExhibitToUserInstitution() {
        // delete images
        testUtils.deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
        // prepare user
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        // prepare institution
        Institution institution = testUtils.createValidInstitution();
        institutionService.saveInstitution(institution, user);
        // prepare exhibit
        MockExhibit exhibit = new MockExhibit("name", "text");

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<Object> response = testRestTemplate.postForEntity("/exhibits", exhibit, Object.class);

        // check right exhibits count
        assertEquals(1, exhibitRepository.count());
        // check info label was stored
        assertEquals(1, testUtils.getFolderContentCount(INFO_LABELS_IMAGES_FOLDER));
        // check user is now institution owner
        assertTrue(userRepository.getById(user.getId()).isInstitutionOwner());
        // check status code
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    /**
     * Tests endpoint for saving exhibit to institution
     */
    @Test
    public void testSaveExhibitToInstitution() {
        // delete images
        testUtils.deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
        // prepare user
        User user = testUtils.createValidUser();
        userService.save(user);
        // prepare institution
        Institution institution = testUtils.createValidInstitution();
        institutionService.saveInstitution(institution, user);
        // prepare exhibit
        MockExhibit exhibit = new MockExhibit("name", "text");

        // call tested endpoint
        ResponseEntity<Object> response = testRestTemplate.postForEntity("/exhibits/" + institution.getId(), exhibit, Object.class);

        // check right exhibits count
        assertEquals(1, exhibitRepository.count());
        // check info label was stored
        assertEquals(1, testUtils.getFolderContentCount(INFO_LABELS_IMAGES_FOLDER));
        // check user is now institution owner
        assertTrue(userRepository.getById(user.getId()).isInstitutionOwner());
        // check status code
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
