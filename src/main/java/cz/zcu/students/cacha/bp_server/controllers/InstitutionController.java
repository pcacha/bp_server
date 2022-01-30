package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.shared.CurrentUser;
import cz.zcu.students.cacha.bp_server.view_models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/institutions")
public class InstitutionController {

    @Autowired
    private InstitutionService institutionService;

    @GetMapping
    public Set<InstitutionVM> getInstitutions() {
        Set<InstitutionVM> institutions = institutionService.getInstitutions();
        return institutions;
    }

    @GetMapping("/ordered")
    public Set<InstitutionVM> getInstitutions(@Valid @RequestBody CoordinatesVM coordinates) {
        Set<InstitutionVM> institutions = institutionService.getInstitutionsOrdered(coordinates);
        return institutions;
    }

    @PostMapping("/myInstitution")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse saveInstitution(@Valid @RequestBody Institution institution, @CurrentUser User user) {
        institutionService.saveInstitution(institution, user);
        return new GenericResponse("Institution saved");
    }

    @GetMapping("/myInstitution/languages")
    public AllowedLanguagesVM getAllowedLanguages(@CurrentUser User user) {
        AllowedLanguagesVM allowedLanguages = institutionService.getAllowedLanguages(user);
        return allowedLanguages;
    }

    @PostMapping("/myInstitution/languages/{languageId}")
    public GenericResponse addLanguage(@PathVariable Long languageId, @CurrentUser User user) {
        institutionService.addLanguage(languageId, user);
        return new GenericResponse("Language added");
    }

    @PutMapping("/myInstitution/updateImage")
    public GenericResponse updateImage(@Valid @RequestBody ImageVM imageVM, @CurrentUser User user) {
        institutionService.updateImage(imageVM, user);
        return new GenericResponse("Image updated");
    }

    @PutMapping("/myInstitution")
    public GenericResponse updateInstitution(@Valid @RequestBody UpdateInstitutionVM updateInstitutionVM, @CurrentUser User user) {
        institutionService.updateInstitution(updateInstitutionVM, user);
        return new GenericResponse("Institution updated");
    }

    @GetMapping("/myInstitution")
    public InstitutionVM getMyInstitution(@CurrentUser User user) {
        InstitutionVM institution = institutionService.getMyInstitution(user);
        return institution;
    }

    @PostMapping("/myInstitution/addManager")
    public GenericResponse addInstitutionManager(@Valid @RequestBody EmailVM emailVM, @CurrentUser User user) {
        institutionService.addInstitutionManager(emailVM, user);
        return new GenericResponse("Email with credentials to a new institution manager account sent");
    }

    @DeleteMapping("/myInstitution")
    public GenericResponse deleteMyInstitution(@CurrentUser User user) {
        institutionService.deleteMyInstitution(user);
        return new GenericResponse("Institution deleted");
    }
}
