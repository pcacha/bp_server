package cz.zcu.students.cacha.bp_server;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static cz.zcu.students.cacha.bp_server.assets_store_config.WebConfiguration.DEFAULT_IMAGE;

/**
 * Utility class for supporting tests
 */
public class TestUtils {

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
    private LanguageRepository languageRepository;

    /**
     * Creates valid system user
     * @return valid user
     */
    public User createValidUser() {
        User user = new User();
        // set valid properties
        user.setUsername("test-user");
        user.setEmail("test-user@email.com");
        user.setPassword("P4ssword");
        return user;
    }

    /**
     * Creates valid translation
     * @return valid translation
     */
    public Translation createValidTranslation() {
        Translation translation = new Translation();
        // set valid properties
        translation.setText("translated text");
        translation.setExhibit(creteValidExhibit());
        translation.setLanguage(languageRepository.findByCode("cs").get());
        return translation;
    }

    /**
     * Creates valid exhibit
     * @return valid exhibit
     */
    public Exhibit creteValidExhibit() {
        Exhibit exhibit = new Exhibit();
        // set valid properties
        exhibit.setName("test name");
        exhibit.setInstitution(creteValidInstitution());
        return exhibit;
    }

    /**
     * Creates valid institution
     * @return valid institution
     */
    public Institution creteValidInstitution() {
        Institution institution = new Institution();
        // set valid properties
        institution.setName("test name");
        institution.setLatitudeString("100");
        institution.setLongitudeString("100");
        // add encoded image
        institution.setEncodedImage(getEncodedImage());
        return institution;
    }

    /**
     * Deletes content of folder
     * @param path path to folder
     */
    public void deleteFolderContent(String path) {
        // get folder and its files
        File folder = new File(path);
        File[] files = folder.listFiles();
        if(files!=null) {
            // delete all files
            for(File f: files) {
                f.delete();
            }
        }
    }

    /**
     * Gets the folder content count
     * @param path path to folder
     * @return count of files
     */
    public int getFolderContentCount(String path) {
        // get folder and its files
        File folder = new File(path);
        File[] files = folder.listFiles();
        // return count
        if(files!=null) {
            return files.length;
        }
        return 0;
    }

    /**
     * Gets encoded image
     * @return encoded image
     */
    public String getEncodedImage() {
        try {
            // return image
            return Base64.getEncoder().encodeToString(new byte[getClass().getClassLoader().getResourceAsStream("static/" + DEFAULT_IMAGE).available()]);
        }
        catch (IOException e) {
            // print stack trace
            System.out.println("Cannot load default image");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * clears all stored images
     */
    public void clearImages() {
        // delete all images from img folders
        deleteFolderContent(INSTITUTIONS_IMAGES_FOLDER);
        deleteFolderContent(EXHIBITS_IMAGES_FOLDER);
        deleteFolderContent(INFO_LABELS_IMAGES_FOLDER);
    }
}
