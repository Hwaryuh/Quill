package io.quill.paper.player;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

final class Metadata {
    private final Map<String, Object> data = Maps.newHashMap();

    <T> Optional<T> get(String key, Class<T> type) {
        Object value = data.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (!type.isInstance(value)) {
            throw new ClassCastException("Expected " + type.getName() + " but got " + value.getClass().getName());
        }
        return Optional.of(type.cast(value));
    }

    void set(String key, Object value) {
        data.put(key, value);
    }

    void remove(String key) {
        data.remove(key);
    }

    boolean has(String key) {
        return data.containsKey(key);
    }
}