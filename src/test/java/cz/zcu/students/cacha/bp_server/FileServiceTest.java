package cz.zcu.students.cacha.bp_server;

import cz.zcu.students.cacha.bp_server.services.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Set of tests to check the quality of FileService class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FileServiceTest {

    /**
     * folder of institutions images
     */
    @Value("${cts.paths.institutions_images_folder}")
    private String INSTITUTIONS_IMAGES_FOLDER;
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
    private FileService fileService;

    @Autowired
    private TestUtils testUtils;

    /**
     * Tests method for saving institution image
     * @throws Exception exception
     */
    @Test
    public void testSaveInstitutionImage() throws Exception {
        // delete all images in folder
        testUtils.deleteFolderContent(INSTITUTIONS_IMAGES_FOLDER);
        // call tested method
        fileService.saveInstitutionImage(testUtils.getEncodedImage());
        // check images count
        assertEquals(1, testUtils.getFolderContentCount(INSTITUTIONS_IMAGES_FOLDER));
    }

    /**
     * Tests method for deleting institution image
     * @throws Exception exception
     */
    @Test
    public void testDeleteInstitutionImage() throws Exception {
        // delete all images in folder
        testUtils.deleteFolderContent(INSTITUTIONS_IMAGES_FOLDER);
        // save institution image
        String name = fileService.saveInstitutionImage(testUtils.getEncodedImage());
        // call tested method
        fileService.deleteInstitutionImage(name);
        // check images count
        assertEquals(0, testUtils.getFolderContentCount(INSTITUTIONS_IMAGES_FOLDER));
    }

    /**
     * Tests method for saving exhibit image
     * @throws Exception exception
     */
    @Test
    public void testSaveExhibitImage() throws Exception {
        // delete all images in folder
        testUtils.deleteFolderContent(EXHIBITS_IMAGES_FOLDER);
        // call tested method
        fileService.saveExhibitImage(testUtils.getEncodedImage());
        // check images count
        assertEquals(1, testUtils.getFolderContentCount(EXHIBITS_IMAGES_FOLDER));
    }

    /**
     * Tests method for deleting exhibit image
     * @throws Exception exception
     */
    @Test
    public void testDeleteExhibitImage() throws Exception {
        // delete all images in folder
        testUtils.deleteFolderContent(EXHIBITS_IMAGES_FOLDER);
        // save exhibit image
        String name = fileService.saveExhibitImage(testUtils.getEncodedImage());
        // call tested method
        fileService.deleteExhibitImage(name);
        // check images count
        assertEquals(0, testUtils.getFolderContentCount(EXHIBITS_IMAGES_FOLDER));
    }

    /**
     * Tests method for saving info label image
     * @throws Exception exception
     */
    @Test
    public void testSaveInfoLabelImage() throws Exception {
        // delete all images in folder
        testUtils.deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
        // call tested method
        fileService.saveInfoLabelImage(testUtils.getEncodedImage());
        assertEquals(1, testUtils.getFolderContentCount(INFO_LABELS_IMAGES_FOLDER));
    }

    /**
     * Tests method for deleting info label image
     * @throws Exception exception
     */
    @Test
    public void testDeleteInfoLabelImage() throws Exception {
        // delete all images in folder
        testUtils.deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
        // save info label image
        String name = fileService.saveInfoLabelImage(testUtils.getEncodedImage());
        // call tested method
        fileService.deleteInfoLabelImage(name);
        // check images count
        assertEquals(0, testUtils.getFolderContentCount(INFO_LABELS_IMAGES_FOLDER));
    }
}
