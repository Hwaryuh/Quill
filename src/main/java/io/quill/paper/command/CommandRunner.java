package io.quill.paper.command;

@FunctionalInterface
public interface CommandRunner<S extends SenderContext> {
    CommandResult run(ExecutionContext<S> ctx);
}