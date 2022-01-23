package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.responses.JWTLoginSuccessResponse;
import cz.zcu.students.cacha.bp_server.services.UserService;
import cz.zcu.students.cacha.bp_server.shared.CurrentUser;
import cz.zcu.students.cacha.bp_server.view_models.UsernamePasswordUpdateVM;
import cz.zcu.students.cacha.bp_server.view_models.UsernamePasswordVM;
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
        return new GenericResponse("Uživatel zaregistrován");
    }

    @PostMapping("/login")
    public JWTLoginSuccessResponse login(@Valid @RequestBody UsernamePasswordVM usernamePasswordVM) {
        JWTLoginSuccessResponse response = userService.login(usernamePasswordVM);
        return response;
    }

    @GetMapping("/token")
    public JWTLoginSuccessResponse token() {
        String jwt = userService.getFreshToken();
        return new JWTLoginSuccessResponse(true, jwt);
    }

    @PostMapping("/updateName")
    public GenericResponse updateName(@Valid @RequestBody UsernamePasswordUpdateVM updateNameVM, @CurrentUser User user) {
        userService.updateName(user, updateNameVM.getUsername());
        return new GenericResponse("Jméno bylo aktualizováno");
    }

}

