package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.services.ExhibitService;
import cz.zcu.students.cacha.bp_server.view_models.ExhibitTranslateVM;
import cz.zcu.students.cacha.bp_server.view_models.ExhibitVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/exhibits")
public class ExhibitController {

    @Autowired
    private ExhibitService exhibitService;

    @GetMapping("/all/{institutionId}")
    public Set<ExhibitVM> getAllExhibitsOfInstitution(@PathVariable Long institutionId) {
        Set<ExhibitVM> exhibits = exhibitService.getExhibitsOfInstitution(institutionId);
        return exhibits;
    }

    @GetMapping("/{exhibitId}")
    public ExhibitTranslateVM getExhibitTranslate(@PathVariable Long exhibitId) {
        ExhibitTranslateVM exhibitTranslate = exhibitService.getExhibitTranslate(exhibitId);
        return exhibitTranslate;
    }
}
