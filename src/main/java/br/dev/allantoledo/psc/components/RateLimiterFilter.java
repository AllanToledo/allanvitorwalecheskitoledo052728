package br.dev.allantoledo.psc.components;

import br.dev.allantoledo.psc.dto.user.UserLoginInformation;
import br.dev.allantoledo.psc.util.SecurityUtility;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RateLimiterFilter implements Filter {

    private static class RateLimiter {
        private int accessCount = 0;
        private long lastRefil = 0;

        public synchronized boolean allowedAccess() {
            long timeToReset = 1000 * 60; // 1 minuto
            if ((System.currentTimeMillis() - lastRefil) >= timeToReset) {
                lastRefil = System.currentTimeMillis();
                accessCount = 10;
            }

            if (accessCount > 0) {
                accessCount--;
                return true;
            }
            return false;
        }
    }

    @Value("${security.rateLimit}")
    private String rateLimit;
    private final Map<UUID, RateLimiter> rateLimiterMap = new HashMap<>();

    @Override
    public void doFilter(
        ServletRequest servletRequest,
        ServletResponse servletResponse,
        FilterChain filterChain
    ) throws IOException, ServletException {
        UserLoginInformation user = SecurityUtility.getUserLoginInformation();
        if (user == null || !rateLimit.equals("enabled")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(user.getId(), (id) -> new RateLimiter());
            if (rateLimiter.allowedAccess()) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
        }
    }
}
