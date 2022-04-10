package cz.zcu.students.cacha.bp_server.services_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.*;
import cz.zcu.students.cacha.bp_server.repositories.*;
import cz.zcu.students.cacha.bp_server.services.ExhibitService;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.services.LocationService;
import cz.zcu.students.cacha.bp_server.view_models.ExhibitVM;
import cz.zcu.students.cacha.bp_server.view_models.ExhibitsLanguagesVM;
import cz.zcu.students.cacha.bp_server.view_models.ImageVM;
import cz.zcu.students.cacha.bp_server.view_models.UpdateExhibitVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Set of tests to check the quality of ExhibitService class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ExhibitServiceTest {

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
    private LocationService locationService;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ShowcaseRepository showcaseRepository;

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
        showcaseRepository.deleteAll();
        roomRepository.deleteAll();
        buildingRepository.deleteAll();
        institutionRepository.deleteAll();

        // delete all images from img folders
        testUtils.clearImages();
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
     * Tests get exhibits of institution method
     */
    @Test
    public void testGetExhibitsOfInstitution() {
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save institution with manager and exhibits
        saveExhibit(user, exhibit);

        // call tested method
        List<ExhibitVM> exhibitsOfInstitution = exhibitService.getExhibitsOfInstitution(exhibit.getInstitution().getId());
        // check right count
        assertEquals(1, exhibitsOfInstitution.size());
    }

    /**
     * Tests delete exhibit method
     */
    @Test
    public void testDeleteExhibit() {
        // delete images
        testUtils.deleteFolderContent(EXHIBITS_IMAGES_FOLDER);
        testUtils.deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // add image
        exhibit.setEncodedImage(testUtils.getEncodedImage());
        // save institution with manager and exhibits
        saveExhibit(user, exhibit);

        // call tested method
        exhibitService.deleteExhibit(exhibit.getId(), user);
        // check there is now no exhibit in db
        assertEquals(0, exhibitRepository.count());
        // check there is no image stored
        assertEquals(0, testUtils.getFolderContentCount(EXHIBITS_IMAGES_FOLDER));
        assertEquals(0, testUtils.getFolderContentCount(INFO_LABELS_IMAGES_FOLDER));
    }

    /**
     * Tests saving exhibit to user institution
     */
    @Test
    public void testSaveExhibitToUserInstitution() {
        // delete images
        testUtils.deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // call tested method - saving to user's institution
        saveExhibit(user, exhibit);

        // check right exhibits count
        assertEquals(1, exhibitRepository.count());
        // check info label was stored
        assertEquals(1, testUtils.getFolderContentCount(INFO_LABELS_IMAGES_FOLDER));
        // check user is now institution owner
        assertTrue(userRepository.getById(user.getId()).isInstitutionOwner());
    }

    /**
     * Tests saving exhibit to institution
     */
    @Test
    public void testSaveExhibitToInstitution() {
        // delete images
        testUtils.deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save institution and manager
        institutionService.saveInstitution(exhibit.getInstitution(), user);
        // add building, room and showcase to db
        Building building = new Building();
        building.setName("testBuilding");
        Room room = new Room();
        room.setName("testRoom");
        Showcase showcase = new Showcase();
        showcase.setName("testShowcase");
        locationService.saveBuilding(building, user);
        locationService.saveRoom(room, building.getId(), user);
        locationService.saveShowcase(showcase, room.getId(), user);
        // add building, room and showcase to created exhibit
        exhibit.setBuildingId(String.valueOf(building.getId()));
        exhibit.setRoomId(String.valueOf(room.getId()));
        exhibit.setShowcaseId(String.valueOf(showcase.getId()));

        // call tested method - saving to institution
        exhibitService.saveExhibit(exhibit, exhibit.getInstitution().getId());

        // check right exhibits count
        assertEquals(1, exhibitRepository.count());
        // check info label was stored
        assertEquals(1, testUtils.getFolderContentCount(INFO_LABELS_IMAGES_FOLDER));
        // check user is now institution owner
        assertTrue(userRepository.getById(user.getId()).isInstitutionOwner());
    }

    /**
     * Tests updating of exhibit image
     */
    @Test
    public void testUpdateExhibitImage() {
        // delete images
        testUtils.deleteFolderContent(EXHIBITS_IMAGES_FOLDER);
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // prepare updated image
        ImageVM imageVM = new ImageVM();
        imageVM.setEncodedImage(testUtils.getEncodedImage());

        // call tested method
        String updatedName = exhibitService.updateExhibitImage(exhibit.getId(), imageVM, user);

        // check exhibit image was stored
        assertEquals(1, testUtils.getFolderContentCount(EXHIBITS_IMAGES_FOLDER));
        // check name was updated
        assertEquals(updatedName, exhibitRepository.findById(exhibit.getId()).get().getImage());
    }

    /**
     * Tests updating of exhibit info label
     */
    @Test
    public void testUpdateExhibitInfoLabel() {
        // delete images
        testUtils.deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // prepare updated image
        ImageVM imageVM = new ImageVM();
        imageVM.setEncodedImage(testUtils.getEncodedImage());

        // call tested method
        String updatedName = exhibitService.updateExhibitInfoLabel(exhibit.getId(), imageVM, user);

        // check exhibit image was stored
        assertEquals(1, testUtils.getFolderContentCount(INFO_LABELS_IMAGES_FOLDER));
        // check name was updated
        assertEquals(updatedName, exhibitRepository.findById(exhibit.getId()).get().getInfoLabel());
    }

    /**
     * Tests updating exhibit info
     */
    @Test
    public void testUpdateExhibit() {
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // prepare updated info
        UpdateExhibitVM updateVM = new UpdateExhibitVM();
        updateVM.setName("testUpdate");
        updateVM.setInfoLabelText("testUpdate");

        // call tested method
        exhibitService.updateExhibit(exhibit.getId(), updateVM, user);
        // check values were updated
        Exhibit updated = exhibitRepository.getById(exhibit.getId());
        assertEquals(updateVM.getName(), updated.getName());
        assertEquals(updateVM.getInfoLabelText(), updated.getInfoLabelText());
    }

    /**
     * Tests method for getting all exhibits of user institution
     */
    @Test
    public void testGetAllExhibitsOfUserInstitution() {
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // call tested method
        List<ExhibitVM> exhibits = exhibitService.getAllExhibitsOfUsersInstitution(userRepository.getById(user.getId()));
        // check right result count
        assertEquals(1, exhibits.size());
    }

    /**
     * Tests method for getting all exhibits for approving translations
     */
    @Test
    public void testGetExhibitsApproveTranslations() {
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // call tested method
        ExhibitsLanguagesVM exhibitsApproveTranslations = exhibitService.getExhibitsApproveTranslations(userRepository.getById(user.getId()));
        // check right result count
        assertEquals(1, exhibitsApproveTranslations.getExhibits().size());
    }


    /**
     * Tests method for getting exhibit
     */
    @Test
    public void testGetExhibit() {
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // call tested method and test it returns exhibit
        assertNotNull(exhibitService.getExhibit(exhibit.getId(), user));
    }

    /**
     * Tests providing qr code of an exhibit
     */
    @Test
    public void testGetExhibitQRCode() {
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // call tested method
        assertNotNull(exhibitService.getExhibitQRCode(exhibit.getId(), user));
    }

    /**
     * Tests method for getting all exhibits and languages to translate
     */
    @Test
    public void testGetExhibitsTranslate() {
        // prepare institution with exhibits
        User user = testUtils.createValidUser();
        Exhibit exhibit = testUtils.createValidExhibit();
        // save exhibit and user as its manager
        saveExhibit(user, exhibit);

        // call tested method
        ExhibitsLanguagesVM exhibitsTranslate = exhibitService.getExhibitsTranslate(exhibit.getInstitution().getId());
        // check right result count
        assertEquals(1, exhibitsTranslate.getExhibits().size());
    }
}
