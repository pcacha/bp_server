package cz.zcu.students.cacha.bp_server.integration_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.*;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.TranslationService;
import cz.zcu.students.cacha.bp_server.services.UserService;
import cz.zcu.students.cacha.bp_server.view_models.NewTranslationVM;
import cz.zcu.students.cacha.bp_server.view_models.TranslationSequenceVM;
import cz.zcu.students.cacha.bp_server.view_models.TranslationVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;

import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.ROLE_TRANSLATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Set of tests to check the quality of TranslationController endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TranslationControllerIntegrationTest {
    @Autowired
    private TranslationService translationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private ExhibitRepository exhibitRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    /**
     * called after each test
     * provides cleanup of db and images
     */
    @AfterEach
    public void cleanup() {
        // delete all data from db
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
     * Prepare translation in the system
     * @param user translation author
     * @param translation translation
     */
    private void prepareTranslation(User user, Translation translation) {
        // set translation author
        user.getRoles().add(roleRepository.findByName(ROLE_TRANSLATOR).get());
        user.setTranslations(new HashSet<>());
        user.getTranslations().add(translation);
        translation.setAuthor(user);
        // save data to db
        institutionRepository.save(translation.getExhibit().getInstitution());
        exhibitRepository.save(translation.getExhibit());
        userService.save(user);
        translationRepository.save(translation);
    }

    /**
     * Tests getting translation sequences
     */
    @Test
    public void testGetSequences() {
        // prepare translation and user in the system
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<List> sequencesResponse = testRestTemplate.getForEntity("/translations/sequences", List.class);
        // check count in list and status code
        assertEquals(HttpStatus.OK, sequencesResponse.getStatusCode());
        assertEquals(1, sequencesResponse.getBody().size());
    }

    /**
     * Tests deletion of a sequence
     */
    @Test
    public void testDeleteSequence() {
        // prepare translation and user in the system
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        testRestTemplate.delete("/translations/sequences/" + translation.getExhibit().getId() + "/" + translation.getLanguage().getId());
        // get sequences and check their count
        List<TranslationSequenceVM> sequences = translationService.getSequences(user);
        assertEquals(0, sequences.size());
    }

    /**
     * test get new translation
     */
    @Test
    public void testGetNewTranslation() {
        // prepare exhibit and user in the system
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        Exhibit exhibit = testUtils.createValidExhibit();
        institutionRepository.save(exhibit.getInstitution());
        exhibitRepository.save(exhibit);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<NewTranslationVM> translationResponse = testRestTemplate.getForEntity("/translations/new/" + exhibit.getId() + "/" + languageRepository.findByCode("cs").get().getId(), NewTranslationVM.class);
        // check if response is ok
        assertNotNull(translationResponse.getBody());
        assertEquals("", translationResponse.getBody().getText());
        // check status code
        assertEquals(HttpStatus.OK, translationResponse.getStatusCode());
    }

    /**
     * tests saving new translation
     */
    @Test
    public void testSaveNewTranslation() {
        // prepare exhibit and user in the system
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        userService.save(user);
        Exhibit exhibit = testUtils.createValidExhibit();
        institutionRepository.save(exhibit.getInstitution());
        exhibitRepository.save(exhibit);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // create translation
        Translation translation = new Translation();
        translation.setText("translated text");

        // call tested endpoint
        ResponseEntity<GenericResponse> response = testRestTemplate.postForEntity("/translations/new/" + exhibit.getId() + "/" + languageRepository.findByCode("cs").get().getId(), translation, GenericResponse.class);
        // check translations count
        assertEquals(1, translationRepository.count());
        // check status code
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    /**
     * tests method for getting official translation
     */
    @Test
    public void testGetOfficialTranslation() {
        // prepare translation (unofficial) and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);
        // set translation as official
        translation.setIsOfficial(true);
        translationRepository.save(translation);

        // call tested endpoint
        ResponseEntity<TranslationVM> response = testRestTemplate.getForEntity("/translations/official/" + translation.getExhibit().getId() + "/" + translation.getLanguage().getCode(), TranslationVM.class);
        // check if translation ids match
        assertEquals(translation.getId(), response.getBody().getTranslationId());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
