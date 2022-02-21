package cz.zcu.students.cacha.bp_server;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import cz.zcu.students.cacha.bp_server.repositories.LanguageRepository;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.view_models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Set of tests to check the quality of InstitutionService class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class InstitutionServiceTest {

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
    }

    /**
     * tests get institutions method
     */
    @Test
    public void testGetInstitutions() {
        // prepare institution
        Institution institution = testUtils.creteValidInstitution();
        institutionRepository.save(institution);

        // call tested method
        List<InstitutionVM> institutions = institutionService.getInstitutions();
        // check institutions count
        assertEquals(1, institutions.size());
    }


    /**
     * tests save institution method
     */
    @Test
    public void testSaveInstitution() {
        // delete images
        testUtils.deleteFolderContent(INSTITUTIONS_IMAGES_FOLDER);
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.creteValidInstitution();

        // call tested method
        institutionService.saveInstitution(institution, user);
        // check expected counts
        assertEquals(1, institutionRepository.count());
        assertEquals(1, testUtils.getFolderContentCount(INSTITUTIONS_IMAGES_FOLDER));
    }

    /**
     * tests get allowed languages method
     */
    @Test
    public void testGetAllowedLanguages() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.creteValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // add language
        institutionService.addLanguage(languageRepository.findByCode("cs").get().getId(), user);

        // call tested method
        AllowedLanguagesVM allowedLanguages = institutionService.getAllowedLanguages(user);
        // check counts
        assertEquals(1, allowedLanguages.getChosenLanguages().size());
        assertTrue(allowedLanguages.getPossibleLanguages().size() > 0);
    }

    /**
     * tests add language method
     */
    @Test
    public void testAddLanguage() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.creteValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);
        institutionService.addLanguage(languageRepository.findByCode("cs").get().getId(), user);
        // check count
        assertEquals(1, institutionRepository.findById(institution.getId()).get().getLanguages().size());
    }

    /**
     * tests update image method
     */
    @Test
    public void testUpdateImage() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.creteValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);
        String oldName = institution.getImage();

        // create updated image
        ImageVM imageVM = new ImageVM();
        imageVM.setEncodedImage(testUtils.getEncodedImage());
        // call tested method
        institutionService.updateImage(imageVM, user);
        // check name is updated
        assertNotEquals(oldName, institutionRepository.findById(institution.getId()).get().getImage());
    }

    /**
     * tests update institution info
     */
    @Test
    public void testUpdateInstitution() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.creteValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // prepare new values
        UpdateInstitutionVM updateVM = new UpdateInstitutionVM();
        updateVM.setName("a");
        updateVM.setAddress("b");
        updateVM.setLatitudeString("10");
        updateVM.setLongitudeString("10");

        // call tested method
        institutionService.updateInstitution(updateVM, user);

        // check that info is updated
        Institution updated = institutionRepository.findById(institution.getId()).get();
        assertEquals(updateVM.getName(), updated.getName());
        assertEquals(updateVM.getAddress(), updated.getAddress());
        assertEquals(Double.parseDouble(updateVM.getLatitudeString()), updated.getLatitude());
        assertEquals(Double.parseDouble(updateVM.getLongitudeString()), updated.getLongitude());
    }

    /**
     * tests get my institution method
     */
    @Test
    public void testGetMyInstitution() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.creteValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // check tested method is not returning null
        assertNotNull(institutionService.getMyInstitution(user));
    }

    /**
     * tests add institution manager method
     */
    @Test
    public void testAddInstitutionManager() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.creteValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // prepare new manager email
        EmailVM emailVM = new EmailVM();
        emailVM.setEmail("pavelcacha@email.cz");

        // check tested method
        assertDoesNotThrow(() -> institutionService.addInstitutionManager(emailVM, user));
        // check new manager is in db
        assertEquals(2, userRepository.count());
    }

    /**
     * tests delete my institution method
     */
    @Test
    public void testDeleteMyInstitution() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.creteValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // call tested method
        institutionService.deleteMyInstitution(userRepository.findById(user.getId()).get());
        // check institutions count
        assertEquals(0, institutionRepository.count());
    }

    /**
     * tests delete institution
     */
    @Test
    public void testDeleteInstitution() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.creteValidInstitution();

        // save institution
        institutionService.saveInstitution(institution, user);

        // call tested method
        institutionService.deleteInstitution(institutionRepository.findById(institution.getId()).get());
        // check institutions count
        assertEquals(0, institutionRepository.count());
    }

    /**
     * tests get institutions ordered
     */
    @Test
    public void testGetInstitutionsOrdered() {
        // prepare institutions
        User user1 = testUtils.createValidUser();
        User user2 = testUtils.createValidUser();
        Institution institution1 = testUtils.creteValidInstitution();
        Institution institution2 = testUtils.creteValidInstitution();

        // set institution's locations
        // Prague
        institution1.setLatitudeString("50.057139");
        institution1.setLongitudeString("14.419556");
        institution1.setName("Prague");
        // Paris
        institution2.setLatitudeString("48.856583");
        institution2.setLongitudeString("2.356567");
        institution2.setName("Paris");

        // save institutions
        institutionService.saveInstitution(institution1, user1);
        institutionService.saveInstitution(institution2, user2);

        // Pilsen location
        CoordinatesVM coordinatesVMPilsen = new CoordinatesVM();
        coordinatesVMPilsen.setLatitude("49.750218");
        coordinatesVMPilsen.setLongitude("13.378601");

        // call tested method
        List<InstitutionVM> institutionsOrderedPilsen = institutionService.getInstitutionsOrdered(coordinatesVMPilsen);
        // check right order to Pilsen
        assertEquals("Prague", institutionsOrderedPilsen.get(0).getName());

        // London location
        CoordinatesVM coordinatesVMLondon = new CoordinatesVM();
        coordinatesVMLondon.setLatitude("51.402633");
        coordinatesVMLondon.setLongitude("0.142822");

        // call tested method
        List<InstitutionVM> institutionsOrderedLondon = institutionService.getInstitutionsOrdered(coordinatesVMLondon);
        // check right order to London
        assertEquals("Paris", institutionsOrderedLondon.get(0).getName());
    }
}
