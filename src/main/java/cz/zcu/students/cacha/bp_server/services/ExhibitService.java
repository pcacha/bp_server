package cz.zcu.students.cacha.bp_server.services;

import cz.zcu.students.cacha.bp_server.domain.Exhibit;
import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.Translation;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.exceptions.CannotPerformActionException;
import cz.zcu.students.cacha.bp_server.exceptions.CannotSaveImageException;
import cz.zcu.students.cacha.bp_server.exceptions.NotFoundException;
import cz.zcu.students.cacha.bp_server.repositories.ExhibitRepository;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import cz.zcu.students.cacha.bp_server.repositories.TranslationRepository;
import cz.zcu.students.cacha.bp_server.view_models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.zcu.students.cacha.bp_server.assets_store_config.WebConfiguration.DEFAULT_EXHIBIT_IMAGE;

@Service
public class ExhibitService {

    @Autowired
    private ExhibitRepository exhibitsRepository;

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private FileService fileService;

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

    public void deleteExhibit(Long exhibitId, User user) {
        Exhibit exhibit = verifyUserManagesExhibit(exhibitId, user);
        deleteExhibit(exhibit);
    }

    public void deleteExhibit(Exhibit exhibit) {
        if(!exhibit.getImage().equals(DEFAULT_EXHIBIT_IMAGE)) {
            fileService.deleteExhibitImage(exhibit.getImage());
        }
        fileService.deleteInfoLabelImage(exhibit.getInfoLabel());

        Iterator<Translation> translationIterator = exhibit.getTranslations().iterator();
        translationIterator.forEachRemaining(t -> translationRepository.delete(t));

        exhibitsRepository.delete(exhibit);
    }

    private Exhibit verifyUserManagesExhibit(Long exhibitId, User user) {
        Optional<Exhibit> exhibitOptional = exhibitsRepository.findById(exhibitId);

        if(exhibitOptional.isEmpty()) {
            throw new NotFoundException("Exhibit not found");
        }
        Exhibit exhibit = exhibitOptional.get();

        if(!user.isInstitutionOwner() || !exhibit.getInstitution().getId().equals(user.getInstitution().getId())) {
            throw new CannotPerformActionException("Logged in user does not have permission to delete this exhibit");
        }

        return exhibit;
    }

    public void saveExhibit(Exhibit exhibit, User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own an institution");
        }

        saveExhibitToInstitution(exhibit, user.getInstitution());
    }

    public void saveExhibit(Exhibit exhibit, Long institutionId) {
        Optional<Institution> institutionOptional = institutionRepository.findById(institutionId);
        if(institutionOptional.isEmpty()) {
            throw new NotFoundException("Institution not found");
        }

        saveExhibitToInstitution(exhibit, institutionOptional.get());
    }

    private void saveExhibitToInstitution(Exhibit exhibit, Institution institution) {
        if(exhibit.getEncodedImage() != null) {
            String imageName;

            try {
                imageName = fileService.saveExhibitImage(exhibit.getEncodedImage());
                exhibit.setImage(imageName);
            } catch (Exception exception) {
                throw new CannotSaveImageException("Image could not be saved");
            }
        }

        String infoLabelName;
        try {
            infoLabelName = fileService.saveInfoLabelImage(exhibit.getEncodedInfoLabel());
            exhibit.setInfoLabel(infoLabelName);
        } catch (Exception exception) {
            throw new CannotSaveImageException("Info label could not be saved");
        }

        exhibit.setInstitution(institution);
        exhibitsRepository.save(exhibit);
    }

    public void updateExhibitImage(Long exhibitId, ImageVM imageVM, User user) {
        Exhibit exhibit = verifyUserManagesExhibit(exhibitId, user);
        if(!exhibit.getImage().equals(DEFAULT_EXHIBIT_IMAGE)) {
            fileService.deleteExhibitImage(exhibit.getImage());
        }

        String imageName;
        try {
            imageName = fileService.saveExhibitImage(imageVM.getEncodedImage());
        } catch (Exception exception) {
            throw new CannotSaveImageException("Image could not be saved");
        }

        exhibit.setImage(imageName);
        exhibitsRepository.save(exhibit);
    }

    public void updateExhibitInfoLabel(Long exhibitId, ImageVM imageVM, User user) {
        Exhibit exhibit = verifyUserManagesExhibit(exhibitId, user);
        fileService.deleteInfoLabelImage(exhibit.getInfoLabel());

        String infoLabelName;
        try {
            infoLabelName = fileService.saveInfoLabelImage(imageVM.getEncodedImage());
        } catch (Exception exception) {
            throw new CannotSaveImageException("Info label could not be saved");
        }

        exhibit.setInfoLabel(infoLabelName);
        exhibitsRepository.save(exhibit);
    }

    public void updateExhibit(Long exhibitId, UpdateExhibitVM updateExhibitVM, User user) {
        Exhibit exhibit = verifyUserManagesExhibit(exhibitId, user);
        exhibit.setName(updateExhibitVM.getName());
        exhibit.setBuilding(updateExhibitVM.getBuilding());
        exhibit.setRoom(updateExhibitVM.getRoom());
        exhibit.setShowcase(updateExhibitVM.getShowcase());
        exhibitsRepository.save(exhibit);
    }

    public Set<ExhibitVM> getAllExhibitsOfUsersInstitution(User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        return user.getInstitution().getExhibits().stream().map(ExhibitVM::new).collect(Collectors.toSet());
    }

    public OfficialTranslationsOverviewVM getOfficialTranslationsOverview(User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        return new OfficialTranslationsOverviewVM(user.getInstitution().getLanguages(), user.getInstitution().getExhibits());
    }
}
