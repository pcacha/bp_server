package cz.zcu.students.cacha.bp_server.selenium_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.integration_tests.MockExhibit;
import cz.zcu.students.cacha.bp_server.repositories.*;
import cz.zcu.students.cacha.bp_server.services.ExhibitService;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Set of tests to check connection between frontend and backend using selenium
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application.properties")
@ActiveProfiles("test")
public class SeleniumTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ExhibitService exhibitService;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private ExhibitRepository exhibitRepository;

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * institution id
     */
    private Long institutionId;
    /**
     * exhibit id
     */
    private Long exhibitId;
    /**
     * language id
     */
    private Long languageId;

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
    }

    /**
     * method for preparing data to create default environment
     */
    private void prepareEnvironment() {
        // create user - institution owner
        User user = testUtils.createValidUser();
        userService.save(user);
        // create institution
        Institution institution = testUtils.createValidInstitution();
        institution.setEncodedImage(null);
        institutionService.saveInstitution(institution, user);
        // add language to institution
        institutionService.addLanguage(languageRepository.findByCode("cs").get().getId(), user);
        // add exhibit
        MockExhibit mockExhibit = new MockExhibit("name", "text", "1", "1", "1");
        Exhibit exhibit = new Exhibit();
        exhibit.setName(mockExhibit.getName());
        exhibit.setInfoLabelText(mockExhibit.getInfoLabelText());
        exhibit.setEncodedInfoLabel(mockExhibit.getEncodedInfoLabel());
        exhibit.setBuilding(mockExhibit.getBuilding());
        exhibit.setRoom(mockExhibit.getRoom());
        exhibit.setShowcase(mockExhibit.getShowcase());
        exhibitService.saveExhibit(exhibit, user);
        // set ids
        institutionId = institutionRepository.findByName(institution.getName()).get().getId();
        exhibitId = exhibitRepository.findAll().get(0).getId();
        languageId = languageRepository.findByCode("cs").get().getId();
    }

    /**
     * tests signing up and sending translation
     */
    @Test
    public void testTranslationCreation() {
        prepareEnvironment();
        SeleniumManager seleniumManager = new SeleniumManager();
        // sing up
        seleniumManager.fillInput("Enter name", "selenium-user");
        seleniumManager.fillInput("Enter e-mail", "seleniumuser@email.com");
        seleniumManager.fillInput("Enter password", "P4ssword");
        seleniumManager.fillInput("Enter password again", "P4ssword");
        seleniumManager.clickBtnWithText("Sign up");
        seleniumManager.waitUntilH1Appears();
        // create translation
        seleniumManager.getURLAndWait("http://localhost:3000/institutions/" + institutionId + "/translate/" + exhibitId + "/" + languageId);
        seleniumManager.changeEditorText("test translation text");
        // send translation
        seleniumManager.clickBtnWithText("Create translation");
        seleniumManager.waitUntilTranslationPage();
        seleniumManager.closeWindow();
        // check that new translation is in db
        assertEquals(1, translationRepository.count());
    }
}
