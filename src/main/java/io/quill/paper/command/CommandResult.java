package io.quill.paper.command;

public sealed interface CommandResult {

    record Success() implements CommandResult { }

    record Failure(String message) implements CommandResult { }

    static CommandResult success() {
        return new Success();
    }

    static CommandResult failure(String message) {
        return new Failure(message);
    }
}