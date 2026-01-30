package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.token.RecoveryToken;
import br.dev.allantoledo.psc.dto.token.TokenInformation;
import br.dev.allantoledo.psc.dto.user.UserInformation;
import br.dev.allantoledo.psc.dto.user.UserLoginInformation;
import br.dev.allantoledo.psc.dto.user.UserNewPassword;
import br.dev.allantoledo.psc.service.AuthenticationService;
import br.dev.allantoledo.psc.service.UserService;
import br.dev.allantoledo.psc.util.SecurityUtility;
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
public class AuthenticationController {


    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Value("${jwt.minutesToExpire}")
    private Integer minutesToExpireToken;

    @GetMapping("/token")
    public TokenInformation getToken() {
        return authenticationService.createAccessToken();
    }

    @PutMapping("/recovery")
    public UserInformation recoveryAccess(
            @RequestParam String token,
            @RequestBody UserNewPassword userNewPassword
    ) {
        String email = authenticationService.getEmailFromRecoveryToken(token);

        return UserInformation.fromUser(userService.updateUserPassword(email, userNewPassword));
    }

    @GetMapping("/recovery")
    public RecoveryToken getRecoveryLink(@RequestParam() String email) {
        return authenticationService.createRecoveryToken(email);
    }
}
