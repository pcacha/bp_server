package cz.zcu.students.cacha.bp_server.services;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.exceptions.NotFoundException;
import cz.zcu.students.cacha.bp_server.repositories.ExhibitRepository;
import cz.zcu.students.cacha.bp_server.view_models.ExhibitTranslateVM;
import cz.zcu.students.cacha.bp_server.view_models.ExhibitVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExhibitService {

    @Autowired
    private ExhibitRepository exhibitsRepository;

    public Set<ExhibitVM> getExhibitsOfInstitution(Long institutionId) {
        Set<ExhibitVM> exhibits = exhibitsRepository.findByInstitutionId(institutionId).stream().map(ExhibitVM::new).collect(Collectors.toSet());
        return exhibits;
    }

    public ExhibitTranslateVM getExhibitTranslate(Long exhibitId) {
        Optional<Exhibit> exhibit = exhibitsRepository.findById(exhibitId);
        if (exhibit.isEmpty()) {
            throw new NotFoundException("Exhibit not found");
        }

        return new ExhibitTranslateVM(exhibit.get());
    }
}
