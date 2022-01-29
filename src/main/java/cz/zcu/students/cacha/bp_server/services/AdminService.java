package cz.zcu.students.cacha.bp_server.services;

import cz.zcu.students.cacha.bp_server.domain.Institution;
import cz.zcu.students.cacha.bp_server.domain.Role;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.exceptions.CannotPerformActionException;
import cz.zcu.students.cacha.bp_server.exceptions.NotFoundException;
import cz.zcu.students.cacha.bp_server.repositories.RoleRepository;
import cz.zcu.students.cacha.bp_server.repositories.UserRepository;
import cz.zcu.students.cacha.bp_server.view_models.BooleanValVM;
import cz.zcu.students.cacha.bp_server.view_models.UserDetailVM;
import cz.zcu.students.cacha.bp_server.view_models.UserVM;
import cz.zcu.students.cacha.bp_server.view_models.UsernameUpdateVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.ROLE_INSTITUTION_OWNER;
import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.ROLE_TRANSLATOR;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private RoleRepository roleRepository;

    public Set<UserVM> getUsers() {
        Set<User> nonAdminUsers = userRepository.getNonAdminUsers();
        return nonAdminUsers.stream().map(UserVM::new).collect(Collectors.toSet());
    }

    public UserDetailVM getUserDetail(Long userId) {
        User user = verifyUser(userId);
        return new UserDetailVM(user);
    }

    public void updateUserUsername(UsernameUpdateVM usernameUpdateVM, Long userId) {
        User user = verifyUser(userId);
        user.setUsername(usernameUpdateVM.getUsername());
        userRepository.save(user);
    }

    private User verifyUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User user = userOptional.get();
        if(user.isAdmin()) {
            throw new CannotPerformActionException("Can not return administrator");
        }

        return user;
    }

    public void updateUserPassword(Long userId) {
        User user = verifyUser(userId);
        String password = UUID.randomUUID().toString();
        user.setPassword(bCryptPasswordEncoder.encode(password));
        emailService.sendSimpleMessage(user.getEmail(), "New password",
                "The password to your account registered in the system for community translations of information texts was changed by the administrator."
                        + " The credentials are now as follows:\n\nusername: " + user.getUsername() + "\n" + "password: " + password +
                        "\n\nYou can change the credentials in profile settings after logging in to the system.");
    }

    public void setTranslator(BooleanValVM booleanValVM, Long userId) {
        User user = verifyUser(userId);
        boolean value = booleanValVM.getValue();

        boolean isTranslator = user.isTranslator();
        if((isTranslator && value) || (!isTranslator && !value)) {
            return;
        }

        if(value) {
            Role role = roleRepository.findByName(ROLE_TRANSLATOR).get();
            user.getRoles().add(role);
        } else {
            user.setRoles(user.getRoles().stream().filter(role -> !role.getName().equals(ROLE_TRANSLATOR)).collect(Collectors.toSet()));
        }

        userRepository.save(user);
    }

    public void removeInstitution(Long userId) {
        User user = verifyUser(userId);

        if(user.getInstitution() == null) {
            throw new CannotPerformActionException("User does not manage an institution");
        }

        Institution institution = user.getInstitution();
        if(institution.getOwners().size() > 1) {
            user.setInstitution(null);
            user.setRoles(user.getRoles().stream().filter(role -> !role.getName().equals(ROLE_INSTITUTION_OWNER)).collect(Collectors.toSet()));
            userRepository.save(user);
        }
        else {
            institutionService.deleteInstitution(institution);
        }
    }

    public void setBan(BooleanValVM booleanValVM, Long userId) {
        User user = verifyUser(userId);
        user.setBanned(booleanValVM.getValue());
        userRepository.save(user);
    }
}
