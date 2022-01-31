package cz.zcu.students.cacha.bp_server.services;

import cz.zcu.students.cacha.bp_server.domain.*;
import cz.zcu.students.cacha.bp_server.exceptions.CannotPerformActionException;
import cz.zcu.students.cacha.bp_server.exceptions.CannotSaveImageException;
import cz.zcu.students.cacha.bp_server.exceptions.NotFoundException;
import cz.zcu.students.cacha.bp_server.repositories.InstitutionRepository;
import cz.zcu.students.cacha.bp_server.repositories.LanguageRepository;
import cz.zcu.students.cacha.bp_server.repositories.RoleRepository;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.view_models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static cz.zcu.students.cacha.bp_server.assets_store_config.WebConfiguration.DEFAULT_INSTITUTION_IMAGE;
import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.ROLE_INSTITUTION_OWNER;
import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.ROLE_TRANSLATOR;

/**
 * Class represent service which is responsible for institution operations
 */
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
    private EmailService emailService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ExhibitService exhibitService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public Set<InstitutionVM> getInstitutions() {
        Set<InstitutionVM> institutions = institutionRepository.findAll().stream().map(InstitutionVM::new).collect(Collectors.toSet());
        return institutions;
    }

    /**
     * Saves institution with given user as a manager
     * @param institution new institution
     * @param user institution manager
     */
    public void saveInstitution(Institution institution, User user) {
        // check if the user already have an institution
        if(user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User already owns an institution");
        }

        // convert string coordinates to double
        institution.setLatitude(Double.parseDouble(institution.getLatitudeString()));
        institution.setLongitude(Double.parseDouble(institution.getLongitudeString()));

        // if institution image is not null, save it in the file system
        if(institution.getEncodedImage() != null) {
            String imageName;

            try {
                imageName = fileService.saveInstitutionImage(institution.getEncodedImage());
                institution.setImage(imageName);
            } catch (Exception exception) {
                throw new CannotSaveImageException("Image could not be saved");
            }
        }

        // save institution
        institutionRepository.save(institution);

        // set user as institution manager and give him appropriate role
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
        } catch (Exception exception) {
            throw new CannotSaveImageException("Image could not be saved");
        }

        institution.setImage(imageName);
        institutionRepository.save(institution);
    }

    public void updateInstitution(UpdateInstitutionVM institution, User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }

        user.getInstitution().setName(institution.getName());
        user.getInstitution().setAddress(institution.getAddress());
        user.getInstitution().setLatitude(institution.getLatitude());
        user.getInstitution().setLongitude(institution.getLongitude());
        institutionRepository.save(user.getInstitution());
    }

    /**
     * Gets user's institution
     * @param user owner
     * @return user's institution
     */
    public InstitutionVM getMyInstitution(User user) {
        // check if user manages an institution
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own an institution");
        }

        return new InstitutionVM(user.getInstitution());
    }

    public void addInstitutionManager(EmailVM emailVM, User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }
        Institution institution = user.getInstitution();

        String username = Long.toString(new Date().getTime());
        while(userRepository.findByUsername(username).isPresent()) {
            username = Long.toString(new Date().getTime());
        }

        String password = UUID.randomUUID().toString();

        User newManager = new User();
        newManager.setUsername(username);
        newManager.setPassword(bCryptPasswordEncoder.encode(password));
        newManager.setEmail(emailVM.getEmail());
        newManager.setInstitution(institution);
        newManager.getRoles().add(roleRepository.findByName(ROLE_TRANSLATOR).get());
        newManager.getRoles().add(roleRepository.findByName(ROLE_INSTITUTION_OWNER).get());
        userRepository.save(newManager);

        emailService.sendSimpleMessage(emailVM.getEmail(), "Institution manager credentials",
                "You have been granted managerial rights to a cultural institution registered in the system for community translations of information texts - "
                        + institution.getName() + ". The credentials are as follows:\n\nusername: " + username + "\n" + "password: " + password +
                        "\n\nYou can change the credentials in profile settings after logging in to the system.");
    }

    public void deleteMyInstitution(User user) {
        if(!user.isInstitutionOwner()) {
            throw new CannotPerformActionException("User does not own institution");
        }
        Institution institution = user.getInstitution();

        deleteInstitution(institution);
    }

    public void deleteInstitution(Institution institution) {
        if(!institution.getImage().equals(DEFAULT_INSTITUTION_IMAGE)) {
            fileService.deleteInstitutionImage(institution.getImage());
        }

        Iterator<Exhibit> exhibitIterator = institution.getExhibits().iterator();
        exhibitIterator.forEachRemaining(e -> exhibitService.deleteExhibit(e));
        institution.getExhibits().clear();

        institution.getLanguages().clear();

        Iterator<User> userIterator = institution.getOwners().iterator();
        userIterator.forEachRemaining(u -> {
            u.getRoles().remove(roleRepository.findByName(ROLE_INSTITUTION_OWNER).get());
            u.setInstitution(null);
            userRepository.save(u);
        });
        institution.getOwners().clear();

        institutionRepository.delete(institution);
    }

    public Set<InstitutionVM> getInstitutionsOrdered(CoordinatesVM coordinates) {
        List<Institution> institutionsList = institutionRepository.findAll();

        institutionsList.forEach(i -> i.setDistance(distance(i.getLatitude(), coordinates.getLatitude(), i.getLongitude(), coordinates.getLongitude(), 0, 0)));
        institutionsList.sort(Comparator.comparingDouble(Institution::getDistance));

        return institutionsList.stream().map(InstitutionVM::new).collect(Collectors.toSet());
    }

    private double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}