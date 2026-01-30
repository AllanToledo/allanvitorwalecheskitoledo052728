package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.token.RecoveryToken;
import br.dev.allantoledo.psc.dto.token.TokenInformation;
import br.dev.allantoledo.psc.dto.user.UserLoginInformation;
import br.dev.allantoledo.psc.util.SecurityUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final Pattern replaceAuthorityPrefix = Pattern.compile("(ROLE|SCOPE)_");
    private final JwtDecoder jwtDecoder;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.minutesToExpire}")
    private Integer minutesToExpireToken;

    public TokenInformation createAccessToken() {
        TokenInformation tokenInformation = new TokenInformation();

        ZonedDateTime expiresAt = ZonedDateTime.now().plusMinutes(minutesToExpireToken);
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

    public String getEmailFromRecoveryToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public RecoveryToken createRecoveryToken(String email) {
        ZonedDateTime expiresAt = ZonedDateTime.now().plusMinutes(30);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .expiresAt(expiresAt.toInstant())
                .issuedAt(ZonedDateTime.now().toInstant())
                .claim("sub", email)
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claims);
        Jwt token = jwtEncoder.encode(parameters);
        RecoveryToken recoveryToken = new RecoveryToken();
        String link = String.format("http://localhost:8080/recovery?token=%s", token.getTokenValue());
        recoveryToken.setLink(link);

        return recoveryToken;
    }
}
