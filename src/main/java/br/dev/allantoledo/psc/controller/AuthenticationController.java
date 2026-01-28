package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.token.TokenInformation;
import br.dev.allantoledo.psc.dto.user.UserInformation;
import br.dev.allantoledo.psc.dto.user.UserLoginInformation;
import br.dev.allantoledo.psc.service.UserService;
import br.dev.allantoledo.psc.util.SecurityUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtEncoder jwtEncoder;
    private final UserService userService;

    private final Pattern replaceAuthorityPrefix = Pattern.compile("(ROLE|SCOPE)_");

    @GetMapping("/token")
    public TokenInformation getToken() {
        TokenInformation tokenInformation = new TokenInformation();

        ZonedDateTime expiresAt = ZonedDateTime.now().plusMinutes(30);
        tokenInformation.setExpiresAt(expiresAt);

        UserLoginInformation user = SecurityUtility.getUserLoginInformation();
        assert user != null;

        String username = user.getUsername();
        String roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(s -> replaceAuthorityPrefix.matcher(s).replaceFirst(""))
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .expiresAt(expiresAt.toInstant())
                .issuedAt(ZonedDateTime.now().toInstant())
                .claim("sub", user.getUsername())
                .claim("scp", roles)
                .claim("id", user.getId())
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claims);
        Jwt token = jwtEncoder.encode(parameters);

        tokenInformation.setToken(token.getTokenValue());
        tokenInformation.setUser(username);
        tokenInformation.setRole(roles);
        return tokenInformation;
    }
}
