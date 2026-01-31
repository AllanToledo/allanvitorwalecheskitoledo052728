package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.user.*;
import br.dev.allantoledo.psc.service.UserService;
import br.dev.allantoledo.psc.util.SecurityUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.dev.allantoledo.psc.util.StreamUtility.mapToList;

@RestController
@RequiredArgsConstructor
@Tag(name = "Usuários")
public class UserController {

    final UserService userService;
    @Operation(
            summary = "Cria um novo usuário.",
            description = """
                    O novo usuário é criado com perfil de usuário comum, é necessário outro administrador elevá-lo
                    ao perfil de administrador caso seja necessário.
                    """
    )
    @PostMapping("/v1/users")
    public UserInformation createUser(@RequestBody UserCreationForm userCreationForm) {
        return UserInformation.fromUser(userService.createUser(userCreationForm));
    }

    @Operation(
            summary = "Usuário acessa a sí mesmo.",
            description = """
                    A rota utiliza informações de login para carregar dados do próprio usuário.
                    """
    )
    @GetMapping("/v1/users/me")
    public UserInformation getSelf() {
        UserLoginInformation userLoginInformation = SecurityUtility.getUserLoginInformation();
        assert userLoginInformation != null;

        return UserInformation.fromUser(userService.getUserById(userLoginInformation.getId()));
    }

    @Operation(
            summary = "Atualiza a sí mesmo.",
            description = """
                    A rota utiliza informações de login para atualizar os dados do próprio usuário.
                    """
    )
    @PutMapping("/v1/users/me")
    public UserInformation secureUpdateUser(
            @RequestBody SecureUserUpdateForm secureUserUpdateForm
    ) {
        UserLoginInformation userLoginInformation = SecurityUtility.getUserLoginInformation();
        assert userLoginInformation != null;

        return UserInformation.fromUser(userService.secureUpdateUser(userLoginInformation.getId(), secureUserUpdateForm));
    }

    @Operation(
            summary = "Lista usuários cadastrados.",
            description = """
                    Requer token de administrador.
                    """,
            parameters = {
                    @Parameter(name = "nameLike", schema = @Schema(type = "string")),
                    @Parameter(name = "emailLike", schema = @Schema(type = "string")),
                    @Parameter(name = "isAdminEqual", schema = @Schema(type = "boolean")),
                    @Parameter(name = "offset",
                            schema = @Schema(
                                    type = "integer",
                                    format = "int32",
                                    minimum = "0",
                                    maximum = "2147483647",
                                    defaultValue = "0"
                            )),
                    @Parameter(name = "limit",
                            schema = @Schema(
                                    type = "integer",
                                    format = "int32",
                                    maximum = "100",
                                    minimum = "0",
                                    defaultValue = "100"
                            ))
            }
    )
    @GetMapping("/v1/users")
    public UserCollection getUserCollection(
            @RequestParam @Parameter(hidden = true) Map<String, String> params
    ) {
        List<UserInformation> users = mapToList(userService.getUserCollection(params), UserInformation::fromUser);
        UserCollection userCollection = new UserCollection();
        userCollection.setUsers(users);
        return userCollection;
    }

    @Operation(
            summary = "Busca dos dados de um usuário por ID",
            description = "Requer token de administrador"
    )
    @GetMapping("/v1/users/{id}")
    public UserInformation getUserCollection(@PathVariable UUID id) {
        return UserInformation.fromUser(userService.getUserById(id));
    }

    @Operation(
            summary = "Atualiza os dados de um usuário",
            description = "Requer token de administrador"
    )
    @PutMapping("/v1/users/{id}")
    public UserInformation adminUpdateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateAdminForm userUpdateAdminForm
    ) {
        return UserInformation.fromUser(userService.adminUpdateUser(id, userUpdateAdminForm));
    }

}
