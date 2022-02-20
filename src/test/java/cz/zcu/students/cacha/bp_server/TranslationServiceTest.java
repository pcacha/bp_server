package cz.zcu.students.cacha.bp_server;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.repositories.*;
import cz.zcu.students.cacha.bp_server.services.TranslationService;
import cz.zcu.students.cacha.bp_server.view_models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Set of tests to check the quality of TranslationService class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TranslationServiceTest {

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
     * Prepare translation in the system
     * @param user translation author
     * @param translation translation
     */
    private void prepareTranslation(User user, Translation translation) {
        // set translation author
        user.setTranslations(new HashSet<>());
        user.getTranslations().add(translation);
        translation.setAuthor(user);
        // save data to db
        institutionRepository.save(translation.getExhibit().getInstitution());
        exhibitRepository.save(translation.getExhibit());
        userRepository.save(user);
        translationRepository.save(translation);
    }

    /**
     * Tests get translation sequences method
     */
    @Test
    public void testGetSequences() {
        // prepare translation and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // get sequences and check their count
        List<TranslationSequenceVM> sequences = translationService.getSequences(user);
        assertEquals(1, sequences.size());
    }

    /**
     * Tests deletion of a sequence
     */
    @Test
    public void testDeleteSequence() {
        // prepare translation and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // delete sequences
        translationService.deleteSequence(translation.getExhibit().getId(), translation.getLanguage().getId(), user);
        // get sequences and check their count
        List<TranslationSequenceVM> sequences = translationService.getSequences(user);
        assertEquals(0, sequences.size());
    }

    /**
     * Tests get translation sequence method
     */
    @Test
    public void testGetSequence() {
        // prepare translation and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // get sequence and check the count
        List<TranslationVM> sequence = translationService.getSequence(translation.getExhibit().getId(), translation.getLanguage().getId(), user);
        assertEquals(1, sequence.size());
    }

    /**
     * Tests translation sequence rollback
     */
    @Test
    public void testRollback() {
        // prepare translation and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);
        // insert second translation
        Translation secondTranslation = new Translation();
        // set valid properties of second translation
        secondTranslation.setText("second text");
        secondTranslation.setExhibit(translation.getExhibit());
        secondTranslation.setLanguage(translation.getLanguage());

        // add translation to user
        secondTranslation.setAuthor(user);
        // save data to db
        translationRepository.save(secondTranslation);
        user.getTranslations().add(secondTranslation);

        // rollback (delete second translation)
        translationService.rollback(translation.getId(), user);

        // get sequence and check the count
        List<TranslationVM> sequence = translationService.getSequence(translation.getExhibit().getId(), translation.getLanguage().getId(), user);
        assertEquals(1, sequence.size());
    }

    /**
     * test get new translation method with translation persisted in db
     */
    @Test
    public void testGetNewTranslationWithTranslationInDB() {
        // prepare translation and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // get newTranslationVM and check if translation texts matches
        NewTranslationVM newTranslationVM = translationService.getNewTranslation(translation.getExhibit().getId(), translation.getLanguage().getId(), user);
        assertEquals(translation.getText(), newTranslationVM.getText());
    }

    /**
     * test get new translation method without translation persisted in db
     */
    @Test
    public void testGetNewTranslationWithoutTranslationInDB() {
        // prepare exhibit and user in the system
        User user = testUtils.createValidUser();
        userRepository.save(user);
        Exhibit exhibit = testUtils.creteValidExhibit();
        institutionRepository.save(exhibit.getInstitution());
        exhibitRepository.save(exhibit);

        // get newTranslationVM and check if translation has no text
        NewTranslationVM newTranslationVM = translationService.getNewTranslation(exhibit.getId(), languageRepository.findByCode("cs").get().getId(), user);
        assertEquals("", newTranslationVM.getText());
    }

    /**
     * tests saving new translation
     */
    @Test
    public void testSaveNewTranslation() {
        // prepare exhibit and user in the system
        User user = testUtils.createValidUser();
        userRepository.save(user);
        Exhibit exhibit = testUtils.creteValidExhibit();
        institutionRepository.save(exhibit.getInstitution());
        exhibitRepository.save(exhibit);

        // create translation
        Translation translation = new Translation();
        translation.setText("translated text");

        // save translation
        translationService.saveNewTranslation(exhibit.getId(), languageRepository.findByCode("cs").get().getId(), translation, user);
        // check translations count
        assertEquals(1, translationRepository.count());
    }

    /**
     * tests method for getting rate overviews
     */
    @Test
    public void testGetRateOverview() {
        // prepare translation and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // get tested rate overview
        RateTranslationsVM rateOverviewVM = translationService.getRateOverview(translation.getExhibit().getId(), translation.getLanguage().getId(), user);
        // check translations count
        assertEquals(1, rateOverviewVM.getTranslations().size());
    }

    /**
     * tests method for setting translations official
     */
    @Test
    public void testSetTranslationOfficial() {
        // prepare translation (official) and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        translation.setIsOfficial(true);
        prepareTranslation(user, translation);
        // set user as the manager of given institution
        user.setInstitution(translation.getExhibit().getInstitution());
        userRepository.save(user);
        // insert second translation (unofficial)
        Translation secondTranslation = new Translation();
        // set valid properties of second translation
        secondTranslation.setText("second text");
        secondTranslation.setExhibit(translation.getExhibit());
        secondTranslation.setLanguage(translation.getLanguage());
        translationRepository.save(secondTranslation);

        // create boolean val container
        BooleanValVM valVM = new BooleanValVM();
        valVM.setValue(true);
        // call tested method
        translationService.setTranslationOfficial(valVM, secondTranslation.getId(), user);
        // check if system changed in the right way
        assertEquals(true, translationRepository.findById(secondTranslation.getId()).get().getIsOfficial());
        assertEquals(false, translationRepository.findById(translation.getId()).get().getIsOfficial());
    }

    /**
     * tests method for getting official translation when there is no official translation
     */
    @Test
    public void testGetOfficialTranslationWhenMissing() {
        // prepare translation (unofficial) and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // check that exception is thrown
        assertThrows(Exception.class, () -> translationService.getOfficialTranslation(translation.getExhibit().getId(), translation.getLanguage().getCode()));
    }

    /**
     * tests method for getting official translation there is official translation
     */
    @Test
    public void testGetOfficialTranslationWhenNotMissing() {
        // prepare translation (unofficial) and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);
        // set translation as official
        translation.setIsOfficial(true);
        translationRepository.save(translation);

        // get official translation using tested method
        TranslationVM translationVM = translationService.getOfficialTranslation(translation.getExhibit().getId(), translation.getLanguage().getCode());
        // check if translation ids match
        assertEquals(translation.getId(), translationVM.getTranslationId());
    }

    /**
     * tests method for setting likes - set like
     */
    @Test
    public void testSetLikeOnSet() {
        // prepare translation (unofficial) and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // create boolean val
        BooleanValVM valVM = new BooleanValVM();
        valVM.setValue(true);

        // set like using tested method
        translationService.setLike(valVM, translation.getId(), user);
        // check if translation is really liked
        assertTrue(translationRepository.findById(translation.getId()).get().getLikers().stream().anyMatch(u -> u.getId().equals(user.getId())));
    }

    /**
     * tests method for setting likes - unset like
     */
    @Test
    public void testSetLikeOnUnset() {
        // prepare translation (unofficial) and user in the system
        User user = testUtils.createValidUser();
        Translation translation = testUtils.createValidTranslation();
        prepareTranslation(user, translation);

        // create boolean val
        BooleanValVM valVM = new BooleanValVM();
        valVM.setValue(true);

        // set like
        translationService.setLike(valVM, translation.getId(), user);
        // unset like using tested method
        valVM.setValue(false);
        translationService.setLike(valVM, translation.getId(), user);
        // check if translation is really not liked
        assertFalse(translationRepository.findById(translation.getId()).get().getLikers().stream().anyMatch(u -> u.getId().equals(user.getId())));
    }
}
