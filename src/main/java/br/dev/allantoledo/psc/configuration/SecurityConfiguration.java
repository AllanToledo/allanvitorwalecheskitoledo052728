package br.dev.allantoledo.psc.configuration;

import org.jspecify.annotations.NonNull;
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static br.dev.allantoledo.psc.util.SecurityUtility.Scopes.*;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain configuration(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) ->
                    authorize
                        .requestMatchers("/error")  .permitAll()
                        .requestMatchers("/docs/**").permitAll()

                        .requestMatchers(HttpMethod.GET,  "/v1/files/covers/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/v1/users")      .permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/recovery")   .permitAll()
                        .requestMatchers(HttpMethod.GET,  "/v1/recovery")   .permitAll()
                        .requestMatchers(HttpMethod.GET,  "/v1/users/me")   .authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/v1/users/me")   .authenticated()
                        .requestMatchers(HttpMethod.GET,  "/v1/token")      .authenticated()
                        .requestMatchers(HttpMethod.GET,  "/v1/users")      .access(hasScope(MANAGER_USERS.name()))
                        .requestMatchers(HttpMethod.GET,  "/v1/users/*")    .access(hasScope(MANAGER_USERS.name()))
                        .requestMatchers(HttpMethod.PUT,  "/v1/users/*")    .access(hasScope(MANAGER_USERS.name()))

                        .requestMatchers(HttpMethod.GET,    "/v1/artists")  .access(hasScope(ACCESS_COLLECTION.name()))
                        .requestMatchers(HttpMethod.GET,    "/v1/artists/*").access(hasScope(ACCESS_COLLECTION.name()))
                        .requestMatchers(HttpMethod.POST,   "/v1/artists")  .access(hasScope(EDIT_COLLECTION.name()))
                        .requestMatchers(HttpMethod.PUT,    "/v1/artists/*").access(hasScope(EDIT_COLLECTION.name()))

                        .requestMatchers(HttpMethod.GET,    "/v1/albums")   .access(hasScope(ACCESS_COLLECTION.name()))
                        .requestMatchers(HttpMethod.GET,    "/v1/albums/*") .access(hasScope(ACCESS_COLLECTION.name()))
                        .requestMatchers(HttpMethod.POST,   "/v1/albums")   .access(hasScope(EDIT_COLLECTION.name()))
                        .requestMatchers(HttpMethod.POST,   "/v1/albums/**").access(hasScope(EDIT_COLLECTION.name()))
                        .requestMatchers(HttpMethod.DELETE, "/v1/covers/*") .access(hasScope(EDIT_COLLECTION.name()))
                        .requestMatchers(HttpMethod.PUT,    "/v1/albums/*") .access(hasScope(EDIT_COLLECTION.name()))

                        .requestMatchers(HttpMethod.GET,    "/v1/files/**") .access(hasScope(ACCESS_COLLECTION.name()))
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

    @Value("${security.allowedOrigins}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                String[] origins = allowedOrigins.split(" ");
                registry
                    .addMapping("/**")
                    .allowedOrigins(origins)
                    .allowedMethods("POST", "GET", "PUT", "DELETE")
                    .allowCredentials(true);
            }
        };
    }
}
