package br.dev.allantoledo.psc.util;

import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Log
public class StringUtility {
    private final static Map<Class<?>, Function<String, ?>> converters;
    static {
        converters = new HashMap<>();
        converters.put(UUID.class, UUID::fromString);
        converters.put(Long.class, Long::valueOf);
        converters.put(String.class, Object::toString);
        converters.put(Integer.class, Integer::valueOf);
        converters.put(Float.class, Float::valueOf);
        converters.put(Double.class, Double::valueOf);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromString(Class<T> clazz, String s) {
        if (s == null) return null;
        if (!converters.containsKey(clazz)) {
            log.severe("Não existe conversor para tipo " + clazz.getName());
            return null;
        }

        try {
            return (T) converters.get(clazz).apply(s);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Não é possível converter '%s' para %s", s, clazz.getName())
            );
        }
    }
}
