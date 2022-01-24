package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.TranslationService;
import cz.zcu.students.cacha.bp_server.shared.CurrentUser;
import cz.zcu.students.cacha.bp_server.view_models.NewTranslationVM;
import cz.zcu.students.cacha.bp_server.view_models.TranslationSequenceVM;
import cz.zcu.students.cacha.bp_server.view_models.TranslationVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/sequence/{id}")
    public GenericResponse rollback(@PathVariable Long id, @CurrentUser User user) {
        translationService.rollback(id, user);
        return new GenericResponse("Rollback processed");
    }

    @GetMapping("/new/{exhibitId}/{languageId}")
    public NewTranslationVM getNewTranslation(@PathVariable Long exhibitId, @PathVariable Long languageId, @CurrentUser User user) {
        NewTranslationVM newTranslationVM = translationService.getNewTranslation(exhibitId, languageId, user);
        return newTranslationVM;
    }

    @PostMapping("/new/{exhibitId}/{languageId}")
    public GenericResponse saveNewTranslation(@PathVariable Long exhibitId, @PathVariable Long languageId, @RequestBody Translation newTranslation, @CurrentUser User user) {
        translationService.saveNewTranslation(exhibitId, languageId, newTranslation, user);
        return new GenericResponse("Translation saved");
    }
}
