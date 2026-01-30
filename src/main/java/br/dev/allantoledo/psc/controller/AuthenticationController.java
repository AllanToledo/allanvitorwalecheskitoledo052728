package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.token.RecoveryToken;
import br.dev.allantoledo.psc.dto.token.TokenInformation;
import br.dev.allantoledo.psc.dto.user.UserInformation;
import br.dev.allantoledo.psc.dto.user.UserLoginInformation;
import br.dev.allantoledo.psc.dto.user.UserNewPassword;
import br.dev.allantoledo.psc.service.AuthenticationService;
import br.dev.allantoledo.psc.service.UserService;
import br.dev.allantoledo.psc.util.SecurityUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Autenticação")
public class AuthenticationController {


    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Operation(
            summary = "Gera um novo token de acesso.",
            description = """
                    A rota pode ser acessada por autenticação com cabeçalho "Authorization: Basic ..." ou
                    "Authorization: Bearer ..." utilizando o token anterior.
                    
                    O token fornecido tem duração de 5 minutos.
                    Cada token é individual e intransferível.
                    """
    )
    @GetMapping("/token")
    public TokenInformation getToken() {
        return authenticationService.createAccessToken();
    }

    @Operation(
            summary = "Atualiza senha do usuário através do link de recuperação.",
            description = """
                    A rota utiliza o token de acesso para permitir o usuário recuperar
                    acesso a conta alterando a senha.
                    """
    )
    @PutMapping("/recovery")
    public UserInformation recoveryAccess(
            @RequestParam String token,
            @RequestBody UserNewPassword userNewPassword
    ) {
        String email = authenticationService.getEmailFromRecoveryToken(token);

        return UserInformation.fromUser(userService.updateUserPassword(email, userNewPassword));
    }

    @Operation(
            summary = "Gera um link autoassinado de recuperação.",
            description = """
                    O link é idealmente enviado por email e permite o usuário definir uma nova senha.
                    """
    )
    @GetMapping("/recovery")
    public RecoveryToken getRecoveryLink(@RequestParam() String email) {
        return authenticationService.createRecoveryToken(email);
    }
}
