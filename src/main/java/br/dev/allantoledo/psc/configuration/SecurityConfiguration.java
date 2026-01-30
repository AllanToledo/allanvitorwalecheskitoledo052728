package br.dev.allantoledo.psc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static br.dev.allantoledo.psc.util.SecurityUtility.Scopes.*;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain configuration(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) ->
                    authorize
                        .requestMatchers("/error")                     .permitAll()
                        .requestMatchers("/docs/**")                   .permitAll()

                        .requestMatchers(HttpMethod.POST, "/users")    .permitAll()
                        .requestMatchers(HttpMethod.POST, "/recovery") .permitAll()
                        .requestMatchers(HttpMethod.GET,  "/recovery") .permitAll()
                        .requestMatchers(HttpMethod.GET,  "/users/me") .authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/users/me") .authenticated()
                        .requestMatchers(HttpMethod.GET,  "/token")    .authenticated()
                        .requestMatchers(HttpMethod.GET,  "/users")    .access(hasScope(MANAGER_USERS.name()))
                        .requestMatchers(HttpMethod.GET,  "/users/*")  .access(hasScope(MANAGER_USERS.name()))
                        .requestMatchers(HttpMethod.PUT,  "/users/*")  .access(hasScope(MANAGER_USERS.name()))

                        .requestMatchers(HttpMethod.GET,  "/artists")  .access(hasScope(ACCESS_COLLECTION.name()))
                        .requestMatchers(HttpMethod.GET,  "/artists/*").access(hasScope(ACCESS_COLLECTION.name()))
                        .requestMatchers(HttpMethod.POST, "/artists")  .access(hasScope(EDIT_COLLECTION.name()))
                        .requestMatchers(HttpMethod.PUT,  "/artists/*").access(hasScope(EDIT_COLLECTION.name()))

                        .requestMatchers(HttpMethod.GET,  "/albums")   .access(hasScope(ACCESS_COLLECTION.name()))
                        .requestMatchers(HttpMethod.GET,  "/albums/*") .access(hasScope(ACCESS_COLLECTION.name()))
                        .requestMatchers(HttpMethod.POST, "/albums")   .access(hasScope(EDIT_COLLECTION.name()))
                        .requestMatchers(HttpMethod.PUT,  "/albums/*") .access(hasScope(EDIT_COLLECTION.name()))
                )
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HS256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HS256");
        return NimbusJwtEncoder.withSecretKey(key).build();
    }
}
