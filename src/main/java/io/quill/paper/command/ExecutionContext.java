package io.quill.paper.command;

public interface ExecutionContext<S extends SenderContext> {
    S sender();
    ArgumentMap arguments();
    String fullCommand();

    default <T> T arg(ArgumentKey<T> key) {
        return arguments().get(key);
    }

    default <T> T argOr(ArgumentKey<T> key, T defaultValue) {
        return arguments().getOptional(key).orElse(defaultValue);
    }
}