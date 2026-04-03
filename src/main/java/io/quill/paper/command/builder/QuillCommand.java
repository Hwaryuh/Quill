package io.quill.paper.command.builder;

import io.quill.paper.command.CommandRunner;
import io.quill.paper.command.argument.ArgumentSpec;
import io.quill.paper.command.builder.QuillCommandBuilder.SenderType;

import java.util.List;

public record QuillCommand(
        String name,
        List<String> aliases,
        String permission,
        SenderType senderType,
        List<ArgumentSpec<?>> arguments,
        CommandRunner<?> runner,
        List<QuillCommand> children
) {

    public boolean isExecutable() {
        return runner != null;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }
}