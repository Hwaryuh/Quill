package io.quill.paper.command.builder;

import com.google.common.collect.Lists;
import io.quill.paper.command.ArgumentKey;
import io.quill.paper.command.CommandRunner;
import io.quill.paper.command.DynamicSuggestions;
import io.quill.paper.command.SenderContext;
import io.quill.paper.command.argument.ArgumentRequirement;
import io.quill.paper.command.argument.ArgumentSpec;
import io.quill.paper.command.argument.ArgType;

import java.util.List;
import java.util.function.Consumer;

public final class QuillCommandBuilder {
    private String name;
    private final List<String> aliases = Lists.newArrayList();
    private String permission;
    private SenderType senderType = SenderType.ANY;
    private final List<ArgumentSpec<?>> arguments = Lists.newArrayList();
    private CommandRunner<?> runner;
    private final List<QuillCommand> children = Lists.newArrayList();

    public enum SenderType { ANY, PLAYER, CONSOLE}

    public QuillCommandBuilder name(String name) {
        this.name = name;
        return this;
    }

    public QuillCommandBuilder aliases(String... aliases) {
        this.aliases.addAll(List.of(aliases));
        return this;
    }

    public QuillCommandBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    public QuillCommandBuilder playerOnly() {
        this.senderType = SenderType.PLAYER;
        return this;
    }

    public QuillCommandBuilder consoleOnly() {
        this.senderType = SenderType.CONSOLE;
        return this;
    }

    public <T> ArgumentBuilder<T> argument(ArgumentKey<T> key, ArgType<T> type) {
        return new ArgumentBuilder<>(this, key, type);
    }

    public QuillCommandBuilder run(CommandRunner<SenderContext.AnySender> runner) {
        this.runner = runner;
        return this;
    }

    public QuillCommandBuilder runPlayer(CommandRunner<SenderContext.PlayerOnly> runner) {
        if (this.senderType != SenderType.PLAYER) {
            throw new IllegalStateException("runPlayer can only be used with playerOnly()");
        }
        this.runner = runner;
        return this;
    }

    public QuillCommandBuilder child(QuillCommand child) {
        this.children.add(child);
        return this;
    }

    public QuillCommandBuilder child(Consumer<QuillCommandBuilder> childBuilder) {
        QuillCommandBuilder child = QuillCommandBuilder.create();
        childBuilder.accept(child);
        this.children.add(child.build());
        return this;
    }

    public QuillCommand build() {
        if (name == null || name.isBlank()) throw new IllegalStateException("Command name cannot be null or blank");

        return new QuillCommand(name, List.copyOf(aliases), permission, senderType, List.copyOf(arguments), runner, List.copyOf(children));
    }

    void addArgumentSpec(ArgumentSpec<?> spec) {
        this.arguments.add(spec);
    }

    public static QuillCommandBuilder create() {
        return new QuillCommandBuilder();
    }

    public static final class ArgumentBuilder<T> {
        private final QuillCommandBuilder parent;
        private final ArgumentKey<T> key;
        private final ArgType<T> type;
        private ArgumentRequirement requirement = ArgumentRequirement.REQUIRED;
        private T defaultValue;
        private DynamicSuggestions suggestions = DynamicSuggestions.empty();

        ArgumentBuilder(QuillCommandBuilder parent, ArgumentKey<T> key, ArgType<T> type) {
            this.parent = parent;
            this.key = key;
            this.type = type;
        }

        public ArgumentBuilder<T> optional(T defaultValue) {
            this.requirement = ArgumentRequirement.OPTIONAL;
            this.defaultValue = defaultValue;
            return this;
        }

        public ArgumentBuilder<T> suggests(DynamicSuggestions provider) {
            this.suggestions = provider;
            return this;
        }

        public ArgumentBuilder<T> suggests(String... suggestions) {
            this.suggestions = DynamicSuggestions.of(suggestions);
            return this;
        }

        public ArgumentBuilder<T> suggests(List<String> suggestions) {
            this.suggestions = DynamicSuggestions.of(suggestions);
            return this;
        }

        public QuillCommandBuilder and() {
            ArgumentSpec<T> spec = new ArgumentSpec<>(key, type, requirement, defaultValue, suggestions);
            parent.addArgumentSpec(spec);
            return parent;
        }
    }
}