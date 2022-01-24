package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.responses.JWTLoginSuccessResponse;
import cz.zcu.students.cacha.bp_server.services.UserService;
import cz.zcu.students.cacha.bp_server.shared.CurrentUser;
import cz.zcu.students.cacha.bp_server.view_models.PasswordUpdateVM;
import cz.zcu.students.cacha.bp_server.view_models.UsernamePasswordVM;
import cz.zcu.students.cacha.bp_server.view_models.UserUpdateVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse register(@Valid @RequestBody User user) {
        userService.save(user);
        return new GenericResponse("User registered");
    }

    @PostMapping("/login")
    public JWTLoginSuccessResponse login(@Valid @RequestBody UsernamePasswordVM usernamePasswordVM) {
        JWTLoginSuccessResponse response = userService.login(usernamePasswordVM);
        return response;
    }

    @GetMapping("/token")
    public JWTLoginSuccessResponse token() {
        JWTLoginSuccessResponse response = userService.getFreshToken();
        return response;
    }

    @PostMapping("/updateUser")
    public GenericResponse updateUser(@Valid @RequestBody UserUpdateVM userUpdateVM, @CurrentUser User user) {
        userService.updateUser(user, userUpdateVM.getUsername(), userUpdateVM.getEmail());
        return new GenericResponse("User updated");
    }

    @PostMapping("/updatePassword")
    public GenericResponse updatePassword(@Valid @RequestBody PasswordUpdateVM passwordUpdateVM, @CurrentUser User user) {
        userService.updatePassword(user, passwordUpdateVM.getPassword());
        return new GenericResponse("Password updated");
    }
}

