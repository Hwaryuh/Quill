package io.quill.paper.command.argument;

import io.quill.paper.command.ArgumentKey;
import io.quill.paper.command.DynamicSuggestions;

public record ArgumentSpec<T>(ArgumentKey<T> key, ArgType<T> type, ArgumentRequirement requirement, T defaultValue, DynamicSuggestions suggestions) {
    public ArgumentSpec {
        if (!key.type().equals(type.javaType())) {
            throw new IllegalArgumentException(
                    "Type mismatch: ArgumentKey<%s> expects %s but ArgType provides %s".formatted(key.name(), key.type().getSimpleName(), type.javaType().getSimpleName())
            );
        }

        if (requirement == ArgumentRequirement.OPTIONAL && defaultValue == null) {
            throw new IllegalArgumentException("Optional argument must have a default value");
        }
    }

    public boolean isRequired() {
        return requirement == ArgumentRequirement.REQUIRED;
    }

    public boolean isOptional() {
        return requirement == ArgumentRequirement.OPTIONAL;
    }
}