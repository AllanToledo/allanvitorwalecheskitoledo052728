package br.dev.allantoledo.psc.components;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
public class CustomTokenResolver implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        String token = request.getParameter("token");
        if (token != null && !token.trim().isEmpty()) {
            return token.trim();
        }

        return null;
    }
}
