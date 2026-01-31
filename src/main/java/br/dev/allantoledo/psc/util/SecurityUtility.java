package br.dev.allantoledo.psc.util;

import br.dev.allantoledo.psc.dto.user.UserLoginInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

@Slf4j
public class SecurityUtility {

    public enum Scopes {
        EDIT_COLLECTION,
        ACCESS_COLLECTION,
        MANAGER_USERS;

        public GrantedAuthority getAuthority() {
            return () -> "SCOPE_" + this.name();
        }
    }

    public static final Collection<GrantedAuthority> adminAuthorities = List.of(
            Scopes.EDIT_COLLECTION.getAuthority(),
            Scopes.ACCESS_COLLECTION.getAuthority(),
            Scopes.MANAGER_USERS.getAuthority()
    );

    public static final Collection<GrantedAuthority> userAuthorities = List.of(
            Scopes.ACCESS_COLLECTION.getAuthority()
    );

    public static UserLoginInformation getUserLoginInformation() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) return null;
        assert authentication.getPrincipal() != null;

        if (authentication.getPrincipal() instanceof UserLoginInformation) {
            return (UserLoginInformation) authentication.getPrincipal();
        }

        if (authentication.getPrincipal() instanceof Jwt) {
            return UserLoginInformation.fromJwt((Jwt) authentication.getPrincipal());
        }

        return null;
    }
}
