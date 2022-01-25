package cz.zcu.students.cacha.bp_server.services;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Language;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.exceptions.NotFoundException;
import cz.zcu.students.cacha.bp_server.exceptions.UnauthorizedException;
import cz.zcu.students.cacha.bp_server.repositories.ExhibitRepository;
import cz.zcu.students.cacha.bp_server.repositories.LanguageRepository;
import cz.zcu.students.cacha.bp_server.repositories.TranslationRepository;
import cz.zcu.students.cacha.bp_server.view_models.NewTranslationVM;
import cz.zcu.students.cacha.bp_server.view_models.TranslationSequenceVM;
import cz.zcu.students.cacha.bp_server.view_models.TranslationTextVM;
import cz.zcu.students.cacha.bp_server.view_models.TranslationVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        if(!Objects.equals(translation.getAuthor().getId(), user.getId())) {
            throw new UnauthorizedException("Translation is not owned by current user");
        }

        return translation;
    }

    public NewTranslationVM getNewTranslation(Long exhibitId, Long languageId, User user) {
        Optional<Exhibit> exhibitOptional = exhibitRepository.findById(exhibitId);
        if(exhibitOptional.isEmpty()) {
            throw new NotFoundException("Exhibit not found");
        }

        Exhibit exhibit = exhibitOptional.get();

        Optional<Translation> translationOptional = translationRepository.getLatestTranslation(user.getId(), exhibitId, languageId);

        if(translationOptional.isEmpty()) {
            return new NewTranslationVM(exhibit, "");
        }

        return new NewTranslationVM(exhibit, translationOptional.get().getText());
    }

    public void saveNewTranslation(Long exhibitId, Long languageId, Translation newTranslation, User user) {
        Optional<Exhibit> exhibitOptional = exhibitRepository.findById(exhibitId);
        if(exhibitOptional.isEmpty()) {
            throw new NotFoundException("Exhibit not found");
        }

        Optional<Language> languageOptional = languageRepository.findById(languageId);
        if(languageOptional.isEmpty()) {
            throw new NotFoundException("Language not found");
        }

        newTranslation.setAuthor(user);
        newTranslation.setExhibit(exhibitOptional.get());
        newTranslation.setLanguage(languageOptional.get());
        translationRepository.save(newTranslation);
    }

    public TranslationTextVM getLatestTranslationText(Long exhibitId, Long languageId, User user) {
        Optional<Translation> translationOptional = translationRepository.getLatestTranslation(user.getId(), exhibitId, languageId);
        if(translationOptional.isEmpty()) {
            return new TranslationTextVM("");
        }

        return new TranslationTextVM(translationOptional.get().getText());
    }
}
