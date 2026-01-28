package br.dev.allantoledo.psc.dto.user;

import br.dev.allantoledo.psc.entity.User;
import br.dev.allantoledo.psc.util.SecurityUtility;
import lombok.Data;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Data
@NullMarked
public class UserLoginInformation implements UserDetails {

    private UUID id;
    private Collection<GrantedAuthority> authorities;
    private String password;
    private String username;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    private UserLoginInformation() {}

    public static UserLoginInformation fromUser(User user) {
        UserLoginInformation userLoginInformation = new UserLoginInformation();

        userLoginInformation.setId(user.getId());
        userLoginInformation.setUsername(user.getEmail());
        userLoginInformation.setPassword(user.getPassword());
        userLoginInformation.setAuthorities(
                user.getIsAdmin() ?
                SecurityUtility.adminAuthorities :
                SecurityUtility.userAuthorities
        );

        return userLoginInformation;
    }

    public static UserLoginInformation fromJwt(Jwt jwt) {
        UserLoginInformation userLoginInformation = new UserLoginInformation();
        userLoginInformation.setId(UUID.fromString(jwt.getClaim("id")));
        userLoginInformation.setUsername(jwt.getClaim("sub"));
        Collection<GrantedAuthority> authorities = Arrays.stream(((String) jwt.getClaim("scp")).split(" "))
                .map(s -> (GrantedAuthority) () -> "SCOPE_" + s)
                .toList();
        userLoginInformation.setAuthorities(authorities);

        return userLoginInformation;
    }
}
