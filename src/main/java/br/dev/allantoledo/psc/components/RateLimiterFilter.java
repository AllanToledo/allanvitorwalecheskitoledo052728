package br.dev.allantoledo.psc.components;

import br.dev.allantoledo.psc.dto.user.UserLoginInformation;
import br.dev.allantoledo.psc.util.SecurityUtility;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Log
@Component
public class RateLimiterFilter implements Filter {

    private static class RateLimiter {
        private int accessCount = 0;
        private long lastRefil = 0;
        private final static long duration = (1000 * 60);

        public synchronized boolean allowedAccess() {
            if ((System.currentTimeMillis() - lastRefil) >= duration) {
                lastRefil = System.currentTimeMillis();
                accessCount = 10;
            }

            if (accessCount > 0) {
                accessCount--;
                return true;
            }
            return false;
        }

        public boolean isOld() {
            return (System.currentTimeMillis() - lastRefil) > duration; // 1 minutos
        }
    }

    @Value("${security.rateLimit}")
    private String rateLimit;
    private final Map<UUID, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();
    private boolean isUserAllowed(UUID id) {
        return rateLimiterMap.computeIfAbsent(id, (k) -> new RateLimiter()).allowedAccess();
    }

    @Scheduled(fixedRate = 1000 * 60 * 15) // 15 minutos
    private void cleanupMap() {
        log.info("Executando limpeza de memória!");
        rateLimiterMap.entrySet().stream()
            .filter(entry -> entry.getValue().isOld())
            .map(Map.Entry::getKey)
            .forEach(rateLimiterMap.keySet()::remove);
    }

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
            if (isUserAllowed(user.getId())) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
        }
    }
}
