package cz.zcu.students.cacha.bp_server.services;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.Language;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.exceptions.CannotPerformActionException;
import cz.zcu.students.cacha.bp_server.exceptions.CannotSaveImageException;
import cz.zcu.students.cacha.bp_server.exceptions.NotFoundException;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import cz.zcu.students.cacha.bp_server.repositories.LanguageRepository;
import cz.zcu.students.cacha.bp_server.repositories.RoleRepository;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.view_models.AllowedLanguagesVM;
import cz.zcu.students.cacha.bp_server.view_models.ImageVM;
import cz.zcu.students.cacha.bp_server.view_models.InstitutionVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.zcu.students.cacha.bp_server.assets_store_config.WebConfiguration.DEFAULT_INSTITUTION_IMAGE;
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
    private LanguageRepository languageRepository;

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

    public AllowedLanguagesVM getAllowedLanguages(User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        Set<Language> possibleLanguages = languageRepository.findAllByOrderByName();
        Set<Language> chosenLanguages = user.getInstitution().getLanguages();
        possibleLanguages.removeAll(chosenLanguages);

        return new AllowedLanguagesVM(possibleLanguages, chosenLanguages);
    }

    public void addLanguage(Long languageId, User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        Optional<Language> languageOptional = languageRepository.findById(languageId);
        if(languageOptional.isEmpty()) {
            throw new NotFoundException("Language not found");
        }

        user.getInstitution().getLanguages().add(languageOptional.get());
        institutionRepository.save(user.getInstitution());
    }

    public void updateImage(ImageVM imageVM, User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        Institution institution = user.getInstitution();
        if(!institution.getImage().equals(DEFAULT_INSTITUTION_IMAGE)) {
            fileService.deleteInstitutionImage(institution.getImage());
        }

        String imageName;
        try {
            imageName = fileService.saveInstitutionImage(imageVM.getEncodedImage());
        } catch (IOException exception) {
            throw new CannotSaveImageException("Image could not be saved");
        }

        institution.setImage(imageName);
        institutionRepository.save(institution);
    }

    public void updateInstitution(Institution institution, User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        user.getInstitution().setName(institution.getName());
        user.getInstitution().setAddress(institution.getAddress());
        user.getInstitution().setLatitude(institution.getLatitude());
        user.getInstitution().setLongitude(institution.getLongitude());
        institutionRepository.save(user.getInstitution());
    }

    public InstitutionVM getMyInstitution(User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        return new InstitutionVM(user.getInstitution());
    }
}