package br.dev.allantoledo.psc.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Optional;

@Slf4j
public class EntityUpdater {
    private EntityUpdater() {}
    public static void apply(Object from, Object to) {
        Field[] fields = from.getClass().getDeclaredFields();
        for(Field field: fields) {
            try {
                field.setAccessible(true);
                Field fieldToUpdate = to.getClass().getDeclaredField(field.getName());
                fieldToUpdate.setAccessible(true);
                Optional<?> optionalValue = (Optional<?>) field.get(from);
                if (optionalValue == null) continue;
                fieldToUpdate.set(to, optionalValue.orElse(null));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(e.getMessage());
            }
        }
    }
}
