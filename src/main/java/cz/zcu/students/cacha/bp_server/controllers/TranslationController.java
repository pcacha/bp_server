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

    @GetMapping("/new/{exhibitId}/{languageId}")
    public NewTranslationVM getNewTranslation(@PathVariable Long exhibitId, @PathVariable Long languageId, @CurrentUser User user) {
        NewTranslationVM newTranslationVM = translationService.getNewTranslation(exhibitId, languageId, user);
        return newTranslationVM;
    }

    @PostMapping("/new/{exhibitId}/{languageId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse saveNewTranslation(@PathVariable Long exhibitId, @PathVariable Long languageId, @Valid @RequestBody Translation newTranslation, @CurrentUser User user) {
        translationService.saveNewTranslation(exhibitId, languageId, newTranslation, user);
        return new GenericResponse("Translation saved");
    }

    @GetMapping("/latest/{exhibitId}/{languageId}")
    public TranslationTextVM getLatestTranslationText(@PathVariable Long exhibitId, @PathVariable Long languageId, @CurrentUser User user) {
        TranslationTextVM translationText = translationService.getLatestTranslationText(exhibitId, languageId, user);
        return translationText;
    }

    @GetMapping("/officialOverview/{exhibitId}/{languageId}")
    public OfficialTranslationsVM getOfficialTranslations(@PathVariable Long exhibitId, @PathVariable Long languageId, @CurrentUser User user) {
        OfficialTranslationsVM officialTranslations = translationService.getOfficialTranslations(exhibitId, languageId, user);
        return officialTranslations;
    }

    @PostMapping("/official/{translationId}")
    public GenericResponse makeTranslationOfficial(@PathVariable Long translationId, @CurrentUser User user) {
        translationService.makeTranslationOfficial(translationId, user);
        return new GenericResponse("Translation made official");
    }

    @GetMapping("/official/{exhibitId}/{languageCode}")
    public TranslationVM getOfficialTranslation(@PathVariable Long exhibitId, @PathVariable String languageCode) {
        TranslationVM translation = translationService.getOfficialTranslation(exhibitId, languageCode);
        return translation;
    }

    @PutMapping("/like/{translationId}")
    public GenericResponse setLike(@Valid @RequestBody BooleanValVM booleanValVM, @PathVariable Long translationId, @CurrentUser User user) {
        translationService.setLike(booleanValVM, translationId, user);
        return new GenericResponse("Like set");
    }
}
