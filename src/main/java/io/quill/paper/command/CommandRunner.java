package io.quill.paper.command;

@FunctionalInterface
public interface CommandRunner {
    CommandResult run(ExecutionContext ctx);
}