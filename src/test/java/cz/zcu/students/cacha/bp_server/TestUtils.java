package cz.zcu.students.cacha.bp_server;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Utility class for supporting tests
 */
public class TestUtils {

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
        return institution;
    }
}
