package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.shared.CurrentUser;
import cz.zcu.students.cacha.bp_server.view_models.InstitutionVM;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse saveInstitution(@Valid @RequestBody Institution institution, @CurrentUser User user) {
        institutionService.saveInstitution(institution, user);
        return new GenericResponse("Institution saved");
    }
}
