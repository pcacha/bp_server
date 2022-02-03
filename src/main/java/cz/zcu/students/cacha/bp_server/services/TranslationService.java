package cz.zcu.students.cacha.bp_server.services;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Language;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.exceptions.CannotPerformActionException;
import cz.zcu.students.cacha.bp_server.exceptions.NotFoundException;
import cz.zcu.students.cacha.bp_server.exceptions.UnauthorizedException;
import cz.zcu.students.cacha.bp_server.repositories.ExhibitRepository;
import cz.zcu.students.cacha.bp_server.repositories.LanguageRepository;
import cz.zcu.students.cacha.bp_server.repositories.TranslationRepository;
import cz.zcu.students.cacha.bp_server.view_models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class represent service which is responsible for translations operations
 */
@Service
public class TranslationService {

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private ExhibitRepository exhibitRepository;

    @Autowired
    private LanguageRepository languageRepository;

    public Set<TranslationSequenceVM> getSequences(User user) {
        Set<Translation> translations =  translationRepository.getSequences(user.getId());
        Set<TranslationSequenceVM> sequenceVMS = translations.stream().map(TranslationSequenceVM::new).collect(Collectors.toSet());
        return sequenceVMS;
    }

    public void deleteSequence(Long exhibitId, Long languageId, User user) {
        translationRepository.deleteSequence(user.getId(), exhibitId, languageId);
    }

    public Set<TranslationVM> getSequence(Long exhibitId, Long languageId, User user) {
        Set<Translation> translations = translationRepository.getSequence(user.getId(), exhibitId, languageId);
        Set<TranslationVM> translationVMS = translations.stream().map(TranslationVM::new).collect(Collectors.toSet());
        return translationVMS;
    }

    public void rollback(Long translationId, User user) {
        Translation translation = verifyTranslation(translationId, user);

        Long exhibitId = translation.getExhibit().getId();
        Long languageId = translation.getLanguage().getId();

        translationRepository.rollback(user.getId(), exhibitId, languageId, translation.getCreatedAt());
    }

    private Translation verifyTranslation(Long translationId, User user) {
        Optional<Translation> translationOptional = translationRepository.findById(translationId);
        if(translationOptional.isEmpty()) {
            throw new NotFoundException("Translation not found");
        }

        Translation translation = translationOptional.get();
        if(!translation.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedException("Translation is not owned by current user");
        }

        return translation;
    }

    /**
     * Gets the information for translation to given language and for given exhibit
     * @param exhibitId exhibit id
     * @param languageId language id
     * @param user translator
     * @return new translation VM
     */
    public NewTranslationVM getNewTranslation(Long exhibitId, Long languageId, User user) {
        // check if params exists
        Exhibit exhibit = verifyExhibitExists(exhibitId);
        Language language = verifyLanguageExists(languageId);

        // get latest translated text if exists
        Optional<Translation> translationOptional = translationRepository.getLatestTranslation(user.getId(), exhibitId, languageId);

        if(translationOptional.isEmpty()) {
            // if no translation exists - text is empty
            return new NewTranslationVM(exhibit, "", language.getName());
        }

        // if translation exists return its text too
        return new NewTranslationVM(exhibit, translationOptional.get().getText(), language.getName());
    }

    /**
     * Checks if exhibit with given id exists and returns it
     * @param exhibitId exhibit id
     * @return found exhibit
     */
    private Exhibit verifyExhibitExists(Long exhibitId) {
        Optional<Exhibit> exhibitOptional = exhibitRepository.findById(exhibitId);
        // if exhibit is empty throw exception
        if(exhibitOptional.isEmpty()) {
            throw new NotFoundException("Exhibit not found");
        }
        return exhibitOptional.get();
    }

    /**
     * Checks if language with given id exists and returns it
     * @param languageId language id
     * @return found langugae
     */
    private Language verifyLanguageExists(Long languageId) {
        Optional<Language> languageOptional = languageRepository.findById(languageId);
        // if language is empty throw exception
        if(languageOptional.isEmpty()) {
            throw new NotFoundException("Language not found");
        }
        return languageOptional.get();
    }

    /**
     * Saves new translation
     * @param exhibitId exhibit id
     * @param languageId language id
     * @param newTranslation new translation
     * @param user translation author
     */
    public void saveNewTranslation(Long exhibitId, Long languageId, Translation newTranslation, User user) {
        // check that parameters exists
        Exhibit exhibit = verifyExhibitExists(exhibitId);
        Language language = verifyLanguageExists(languageId);

        // set translation parameters
        newTranslation.setAuthor(user);
        newTranslation.setExhibit(exhibit);
        newTranslation.setLanguage(language);
        // save new translation
        translationRepository.save(newTranslation);
    }

    public OfficialTranslationsVM getOfficialTranslations(Long exhibitId, Long languageId, User user) {
        Optional<Exhibit> exhibitOptional = exhibitRepository.findById(exhibitId);
        if(exhibitOptional.isEmpty()) {
            throw new NotFoundException("Exhibit not found");
        }
        Exhibit exhibit = exhibitOptional.get();

        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        if(!exhibit.getInstitution().getId().equals(user.getInstitution().getId())) {
            throw new CannotPerformActionException("User does not manage this exhibit");
        }

        Optional<Language> languageOptional = languageRepository.findById(languageId);
        if(languageOptional.isEmpty()) {
            throw new NotFoundException("Language not found");
        }

        Set<Translation> latestTranslations = translationRepository.getLatestTranslations(exhibitId, languageId);
        Optional<Translation> officialOptional = translationRepository.getOfficialTranslation(exhibitId, languageId);

        if(officialOptional.isPresent() && !latestTranslations.contains(officialOptional.get())) {
            latestTranslations.add(officialOptional.get());
        }

        return new OfficialTranslationsVM(latestTranslations, exhibit, user);
    }

    public void makeTranslationOfficial(Long translationId, User user) {
        Optional<Translation> translationOptional = translationRepository.findById(translationId);
        if(translationOptional.isEmpty()) {
            throw new NotFoundException("Translation not found");
        }
        Translation translation = translationOptional.get();

        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        if(!translation.getExhibit().getInstitution().getId().equals(user.getInstitution().getId())) {
            throw new CannotPerformActionException("User does not manage this exhibit");
        }

        if(translation.getIsOfficial()) {
            return;
        }

        Optional<Translation> officialOptional = translationRepository.getOfficialTranslation(translation.getExhibit().getId(), translation.getLanguage().getId());

        if(officialOptional.isPresent()) {
            officialOptional.get().setIsOfficial(false);
            translationRepository.save(officialOptional.get());
        }

        translation.setIsOfficial(true);
        translationRepository.save(translation);
    }

    public TranslationVM getOfficialTranslation(Long exhibitId, String languageCode) {
        Optional<Exhibit> exhibitOptional = exhibitRepository.findById(exhibitId);
        if(exhibitOptional.isEmpty()) {
            throw new NotFoundException("Exhibit not found");
        }

        Optional<Language> languageOptional = languageRepository.findByCode(languageCode);
        if(languageOptional.isEmpty()) {
            throw new NotFoundException("Language not found");
        }

        Optional<Translation> officialOptional = translationRepository.getOfficialTranslation(exhibitId, languageOptional.get().getId());
        if(officialOptional.isEmpty()) {
            throw new NotFoundException("Official translation not found");
        }

        return new TranslationVM(officialOptional.get());
    }

    public void setLike(BooleanValVM booleanValVM, Long translationId, User user) {
        Optional<Translation> translationOptional = translationRepository.findById(translationId);
        if(translationOptional.isEmpty()) {
            throw new NotFoundException("Translation not found");
        }
        Translation translation = translationOptional.get();

        boolean userLikes = translation.getLikers().stream().anyMatch(u -> u.getId().equals(user.getId()));
        boolean value = booleanValVM.getValue();
        if((userLikes && value) || (!userLikes && !value)) {
            return;
        }

        if(value) {
            translation.getLikers().add(user);
        }
        else {
            translation.getLikers().remove(user);
        }
        translationRepository.save(translation);
    }
}
