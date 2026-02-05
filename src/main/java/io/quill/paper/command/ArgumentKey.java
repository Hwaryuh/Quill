package io.quill.paper.command;

public record ArgumentKey<T>(String name, Class<T> type) {

    public static <T> ArgumentKey<T> of(String name, Class<T> type) {
        return new ArgumentKey<>(name, type);
    }

    public static ArgumentKey<Integer> intKey(String name) {
        return new ArgumentKey<>(name, Integer.class);
    }

    public static ArgumentKey<String> stringKey(String name) {
        return new ArgumentKey<>(name, String.class);
    }

    public static ArgumentKey<Double> doubleKey(String name) {
        return new ArgumentKey<>(name, Double.class);
    }

    public static ArgumentKey<Boolean> boolKey(String name) {
        return new ArgumentKey<>(name, Boolean.class);
    }
}