package br.dev.allantoledo.psc.components;

import br.dev.allantoledo.psc.dto.user.UserLoginInformation;
import br.dev.allantoledo.psc.util.SecurityUtility;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    static <K, V> Stream<K> filterMap(Map<K, V> map, Predicate<V> predicate) {
        return map.entrySet().stream()
                .filter(entry -> predicate.test(entry.getValue()))
                .map(Map.Entry::getKey);
    }


    @Value("${security.rateLimit}")
    private String rateLimit;
    private final Map<UUID, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();
    private long lastCheck = 0;
    private synchronized boolean isUserAllowed(UUID id) {
        if ((System.currentTimeMillis() - lastCheck) > RateLimiter.duration) {
            lastCheck = System.currentTimeMillis();
            filterMap(rateLimiterMap, RateLimiter::isOld).forEach(rateLimiterMap.keySet()::remove);
        }
        return rateLimiterMap.computeIfAbsent(id, (k) -> new RateLimiter()).allowedAccess();
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
