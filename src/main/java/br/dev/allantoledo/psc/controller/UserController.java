package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.InfoUser;
import br.dev.allantoledo.psc.dto.NewUser;
import br.dev.allantoledo.psc.dto.UpdatedUser;
import br.dev.allantoledo.psc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    final UserService userService;

    @PostMapping("/users")
    public InfoUser createUser(@RequestBody NewUser newUser) {
        return InfoUser.fromUser(userService.createUser(newUser));
    }

    @PutMapping("/users/{id}")
    public InfoUser updateUser(
            @PathVariable UUID id,
            @RequestBody UpdatedUser updatedUser
    ) {
        return InfoUser.fromUser(userService.updateUser(id, updatedUser));
    }
}
