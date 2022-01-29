package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.AdminService;
import cz.zcu.students.cacha.bp_server.view_models.BooleanValVM;
import cz.zcu.students.cacha.bp_server.view_models.UserDetailVM;
import cz.zcu.students.cacha.bp_server.view_models.UserVM;
import cz.zcu.students.cacha.bp_server.view_models.UsernameUpdateVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public Set<UserVM> getUsers() {
        Set<UserVM> users = adminService.getUsers();
        return users;
    }

    @GetMapping("/users/{userId}")
    public UserDetailVM getUserDetail(@PathVariable Long userId) {
        UserDetailVM user = adminService.getUserDetail(userId);
        return user;
    }

    @PutMapping("/users/{userId}/updateUsername")
    public GenericResponse updateUserUsername(@Valid @RequestBody UsernameUpdateVM usernameUpdateVM, @PathVariable Long userId) {
        adminService.updateUserUsername(usernameUpdateVM, userId);
        return new GenericResponse("Username updated");
    }

    @PutMapping("/users/{userId}/updatePassword")
    public GenericResponse updateUserPassword(@PathVariable Long userId) {
        adminService.updateUserPassword(userId);
        return new GenericResponse("New password send to mail box");
    }

    @PutMapping("/users/{userId}/updateTranslator")
    public GenericResponse setTranslator(@Valid @RequestBody BooleanValVM booleanValVM, @PathVariable Long userId) {
        adminService.setTranslator(booleanValVM, userId);
        return new GenericResponse("Translator rights set");
    }

    @PutMapping("/users/{userId}/removeInstitution")
    public GenericResponse removeInstitution(@PathVariable Long userId) {
        adminService.removeInstitution(userId);
        return new GenericResponse("Institution removed");
    }

    @PutMapping("/users/{userId}/updateBan")
    public GenericResponse setBan(@Valid @RequestBody BooleanValVM booleanValVM, @PathVariable Long userId) {
        adminService.setBan(booleanValVM, userId);
        return new GenericResponse("Ban set");
    }
}
