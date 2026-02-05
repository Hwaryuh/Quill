package io.quill.paper.command;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

public final class ArgumentMap {
    private final Map<ArgumentKey<?>, Object> values;

    private ArgumentMap(Map<ArgumentKey<?>, Object> values) {
        this.values = ImmutableMap.copyOf(values);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ArgumentKey<T> key) {
        Object value = values.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Required argument '%s' not found".formatted(key.name()));
        }
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptional(ArgumentKey<T> key) {
        return Optional.ofNullable((T) values.get(key));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<ArgumentKey<?>, Object> values = Maps.newHashMap();

        public <T> Builder put(ArgumentKey<T> key, T value) {
            values.put(key, value);
            return this;
        }

        public ArgumentMap build() {
            return new ArgumentMap(values);
        }
    }
}