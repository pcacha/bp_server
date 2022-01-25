package cz.zcu.students.cacha.bp_server.services;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.exceptions.CannotPerformActionException;
import cz.zcu.students.cacha.bp_server.exceptions.CannotSaveImageException;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import cz.zcu.students.cacha.bp_server.repositories.RoleRepository;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.view_models.InstitutionVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.ROLE_INSTITUTION_OWNER;
import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.ROLE_TRANSLATOR;

@Service
public class InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    public Set<InstitutionVM> getInstitutions() {
        Set<InstitutionVM> institutions = institutionRepository.findAll().stream().map(InstitutionVM::new).collect(Collectors.toSet());
        return institutions;
    }

    public void saveInstitution(Institution institution, User user) {
        if(user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User already owns an institution");
        }

        if(institution.getEncodedImage() != null) {
            String imageName;

            try {
                imageName = fileService.saveInstitutionImage(institution.getEncodedImage());
                institution.setImage(imageName);
            } catch (IOException exception) {
                throw new CannotSaveImageException("Image could not be saved");
            }
        }

        institutionRepository.save(institution);
        user.setInstitution(institution);
        user.getRoles().add(roleRepository.findByName(ROLE_INSTITUTION_OWNER).get());
        userRepository.save(user);
    }
}
