package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.UserInformation;
import br.dev.allantoledo.psc.dto.UserCreationForm;
import br.dev.allantoledo.psc.dto.UserUpdateForm;
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
    public UserInformation updateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateForm userUpdateForm
    ) {
        return UserInformation.fromUser(userService.updateUser(id, userUpdateForm));
    }
}
