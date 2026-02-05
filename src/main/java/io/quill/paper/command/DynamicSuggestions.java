package io.quill.paper.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@FunctionalInterface
public interface DynamicSuggestions {
    List<String> provide(CommandContext<CommandSourceStack> ctx);

    static DynamicSuggestions empty() {
        return ctx -> List.of();
    }

    static DynamicSuggestions of(String... suggestions) {
        return ctx -> List.of(suggestions);
    }

    static DynamicSuggestions of(List<String> suggestions) {
        return ctx -> suggestions;
    }

    default SuggestionProvider<CommandSourceStack> toBrigadier() {
        return (ctx, builder) -> {
            List<String> suggestions = provide(ctx);
            suggestions.forEach(builder::suggest);
            return builder.buildFuture();
        };
    }
}