package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.user.*;
import br.dev.allantoledo.psc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    final UserService userService;

    @PostMapping("/users")
    public UserInformation createUser(@RequestBody UserCreationForm userCreationForm) {
        return UserInformation.fromUser(userService.createUser(userCreationForm));
    }

    @PutMapping("/users/{id}")
    public UserInformation adminUpdateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateAdminForm userUpdateAdminForm
    ) {
        return UserInformation.fromUser(userService.adminUpdateUser(id, userUpdateAdminForm));
    }

}
