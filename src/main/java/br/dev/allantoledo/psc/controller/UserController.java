package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.user.*;
import br.dev.allantoledo.psc.service.UserService;
import br.dev.allantoledo.psc.util.SecurityUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;

@RestController
@RequiredArgsConstructor
public class UserController {

    final UserService userService;

    @PostMapping("/users")
    public UserInformation createUser(@RequestBody UserCreationForm userCreationForm) {
        return UserInformation.fromUser(userService.createUser(userCreationForm));
    }

    @GetMapping("/users/me")
    public UserInformation getSelf() {
        UserLoginInformation userLoginInformation = SecurityUtility.getUserLoginInformation();
        assert userLoginInformation != null;

        return UserInformation.fromUser(userService.getUserById(userLoginInformation.getId()));
    }

    @PutMapping("/users/me")
    public UserInformation secureUpdateUser(
            @RequestBody SecureUserUpdateForm secureUserUpdateForm
    ) {
        UserLoginInformation userLoginInformation = SecurityUtility.getUserLoginInformation();
        assert userLoginInformation != null;

        return UserInformation.fromUser(userService.secureUpdateUser(userLoginInformation.getId(), secureUserUpdateForm));
    }

    @GetMapping("/users")
    public UserCollection getUserCollection(
            @RequestParam Map<String, String> params
    ) {
        List<UserInformation> users = mapToList(userService.getUserCollection(params), UserInformation::fromUser);
        UserCollection userCollection = new UserCollection();
        userCollection.setUsers(users);
        return userCollection;
    }

    @GetMapping("/users/{id}")
    public UserInformation getUserCollection(@PathVariable UUID id) {
        return UserInformation.fromUser(userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public UserInformation adminUpdateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateAdminForm userUpdateAdminForm
    ) {
        return UserInformation.fromUser(userService.adminUpdateUser(id, userUpdateAdminForm));
    }

}
