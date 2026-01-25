package br.dev.allantoledo.psc.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamUtility {
    public static <T, V> Set<V> mapToSet(Collection<T> collection, Function<T, V> map) {
        return collection.stream().map(map).collect(Collectors.toSet());
    }

    public static <T, V> List<V> mapToList(Collection<T> collection, Function<T, V> map) {
        return collection.stream().map(map).collect(Collectors.toList());
    }
}
