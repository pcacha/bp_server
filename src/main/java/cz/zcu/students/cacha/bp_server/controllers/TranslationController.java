package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.TranslationService;
import cz.zcu.students.cacha.bp_server.shared.CurrentUser;
import cz.zcu.students.cacha.bp_server.view_models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

/**
 * Class represents rest controller which is responsible for translations operations
 */
@RestController
@RequestMapping("/translations")
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @GetMapping("/sequences")
    public Set<TranslationSequenceVM> getSequences(@CurrentUser User user) {
        Set<TranslationSequenceVM> sequences = translationService.getSequences(user);
        return sequences;
    }

    @DeleteMapping("/sequences/{exhibitId}/{languageId}")
    public GenericResponse deleteSequence(@PathVariable Long exhibitId, @PathVariable Long languageId, @CurrentUser User user) {
        translationService.deleteSequence(exhibitId, languageId, user);
        return new GenericResponse("Sequence deleted");
    }

    @GetMapping("/sequence/{exhibitId}/{languageId}")
    public Set<TranslationVM> getSequence(@PathVariable Long exhibitId, @PathVariable Long languageId, @CurrentUser User user) {
        Set<TranslationVM> translations = translationService.getSequence(exhibitId, languageId, user);
        return translations;
    }

    @DeleteMapping("/sequence/{translationId}")
    public GenericResponse rollback(@PathVariable Long translationId, @CurrentUser User user) {
        translationService.rollback(translationId, user);
        return new GenericResponse("Rollback processed");
    }

    /**
     * Gets the information for translation to given language and for given exhibit
     * @param exhibitId translated exhibit id
     * @param languageId translation language id
     * @param user logged in user
     * @return new translation VM
     */
    @GetMapping("/new/{exhibitId}/{languageId}")
    public NewTranslationVM getNewTranslation(@PathVariable Long exhibitId, @PathVariable Long languageId, @CurrentUser User user) {
        NewTranslationVM newTranslationVM = translationService.getNewTranslation(exhibitId, languageId, user);
        return newTranslationVM;
    }

    /**
     * Saves new translation
     * @param exhibitId exhibit id
     * @param languageId language id
     * @param newTranslation translation to save
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @PostMapping("/new/{exhibitId}/{languageId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse saveNewTranslation(@PathVariable Long exhibitId, @PathVariable Long languageId, @Valid @RequestBody Translation newTranslation, @CurrentUser User user) {
        translationService.saveNewTranslation(exhibitId, languageId, newTranslation, user);
        return new GenericResponse("Translation saved");
    }

    /**
     * Gets the translation overview for rating
     * @param exhibitId exhibit id
     * @param languageId language id
     * @param user logged in user
     * @return translation overview for rating
     */
    @GetMapping("/rate/{exhibitId}/{languageId}")
    public RateTranslationsVM getRateOverview(@PathVariable Long exhibitId, @PathVariable Long languageId, @CurrentUser User user) {
        RateTranslationsVM rateTranslationsVM = translationService.getRateOverview(exhibitId, languageId, user);
        return rateTranslationsVM;
    }

    /**
     * Set translation official or unofficial based on given value
     * @param booleanValVM value if is official
     * @param translationId translation id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @PutMapping("/official/{translationId}")
    public GenericResponse setTranslationOfficial(@Valid @RequestBody BooleanValVM booleanValVM, @PathVariable Long translationId, @CurrentUser User user) {
        translationService.setTranslationOfficial(booleanValVM, translationId, user);
        return new GenericResponse("Official translation set");
    }

    @GetMapping("/official/{exhibitId}/{languageCode}")
    public TranslationVM getOfficialTranslation(@PathVariable Long exhibitId, @PathVariable String languageCode) {
        TranslationVM translation = translationService.getOfficialTranslation(exhibitId, languageCode);
        return translation;
    }

    /**
     * Set like or dislike from logged in user to given translation
     * @param booleanValVM like
     * @param translationId translation id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @PutMapping("/like/{translationId}")
    public GenericResponse setLike(@Valid @RequestBody BooleanValVM booleanValVM, @PathVariable Long translationId, @CurrentUser User user) {
        translationService.setLike(booleanValVM, translationId, user);
        return new GenericResponse("Like set");
    }
}
