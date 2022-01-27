package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.ExhibitService;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.shared.CurrentUser;
import cz.zcu.students.cacha.bp_server.view_models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/exhibits")
public class ExhibitController {

    @Autowired
    private ExhibitService exhibitService;

    @Autowired
    private InstitutionService institutionService;

    @GetMapping("/all/{institutionId}")
    public Set<ExhibitVM> getAllExhibitsOfInstitution(@PathVariable Long institutionId) {
        Set<ExhibitVM> exhibits = exhibitService.getExhibitsOfInstitution(institutionId);
        return exhibits;
    }

    @GetMapping("/all/myInstitution")
    public Set<ExhibitVM> getAllExhibitsOfUsersInstitution(@CurrentUser User user) {
        Set<ExhibitVM> exhibits = exhibitService.getAllExhibitsOfUsersInstitution(user);
        return exhibits;
    }

    @GetMapping("/{exhibitId}")
    public ExhibitTranslateVM getExhibitTranslate(@PathVariable Long exhibitId) {
        ExhibitTranslateVM exhibitTranslate = exhibitService.getExhibitTranslate(exhibitId);
        return exhibitTranslate;
    }

    @DeleteMapping("/{exhibitId}")
    public GenericResponse deleteExhibit(@PathVariable Long exhibitId, @CurrentUser User user) {
        exhibitService.deleteExhibit(exhibitId, user);
        return new GenericResponse("Exhibit deleted");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse saveExhibit(@Valid @RequestBody Exhibit exhibit, @CurrentUser User user) {
        exhibitService.saveExhibit(exhibit, user);
        return new GenericResponse("Exhibit saved");
    }

    @PutMapping("/{exhibitId}/updateImage")
    public GenericResponse updateExhibitImage(@PathVariable Long exhibitId, @Valid @RequestBody ImageVM imageVM, @CurrentUser User user) {
        exhibitService.updateExhibitImage(exhibitId, imageVM, user);
        return new GenericResponse("Image updated");
    }

    @PutMapping("/{exhibitId}/updateInfoLabel")
    public GenericResponse updateExhibitInfoLabel(@PathVariable Long exhibitId, @Valid @RequestBody ImageVM imageVM, @CurrentUser User user) {
        exhibitService.updateExhibitInfoLabel(exhibitId, imageVM, user);
        return new GenericResponse("Info label updated");
    }

    @PutMapping("/{exhibitId}")
    public GenericResponse updateExhibit(@PathVariable Long exhibitId, @Valid @RequestBody UpdateExhibitVM updateExhibitVM, @CurrentUser User user) {
        exhibitService.updateExhibit(exhibitId, updateExhibitVM, user);
        return new GenericResponse("Exhibit updated");
    }

    @GetMapping("/approveTranslations/myInstitution")
    public OfficialTranslationsOverviewVM getOfficialTranslationsOverview(@CurrentUser User user) {
        OfficialTranslationsOverviewVM officialTranslationsOverviewVM = exhibitService.getOfficialTranslationsOverview(user);
        return officialTranslationsOverviewVM;
    }
}
