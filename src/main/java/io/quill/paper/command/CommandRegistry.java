package io.quill.paper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.math.BlockPosition;
import io.quill.paper.command.argument.ArgType;
import io.quill.paper.command.argument.ArgumentSpec;
import io.quill.paper.command.builder.QuillCommand;
import io.quill.paper.command.builder.QuillCommandBuilder;
import io.quill.paper.util.bukkit.Locations;
import io.quill.paper.util.bukkit.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class CommandRegistry {
    private final Commands commands;

    public CommandRegistry(Commands commands) {
        this.commands = commands;
    }

    public void register(QuillCommand node) {
        LiteralCommandNode<CommandSourceStack> mainNode = buildNode(node.name(), node);
        commands.getDispatcher().getRoot().addChild(mainNode);

        for (String alias : node.aliases()) {
            LiteralCommandNode<CommandSourceStack> aliasNode = Commands.literal(alias).redirect(mainNode).build();
            commands.getDispatcher().getRoot().addChild(aliasNode);
        }
    }

    private LiteralCommandNode<CommandSourceStack> buildNode(String literal, QuillCommand node) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(literal);

        if (node.permission() != null) {
            builder.requires(source -> source.getSender().hasPermission(node.permission()));
        }

        builder.requires(source -> checkSenderType(source, node.senderType()));

        if (!node.arguments().isEmpty()) {
            buildArgumentChain(builder, node, 0);
        } else if (node.isExecutable()) {
            builder.executes(ctx -> run(ctx, node, List.of()));
        }

        for (QuillCommand child : node.children()) {
            LiteralCommandNode<CommandSourceStack> childNode = buildNode(child.name(), child);
            builder.then(childNode);
        }

        return builder.build();
    }

    private void buildArgumentChain(ArgumentBuilder<CommandSourceStack, ?> builder, QuillCommand node, int argIndex) {
        if (argIndex >= node.arguments().size()) {
            if (node.isExecutable()) {
                builder.executes(ctx -> run(ctx, node, node.arguments()));
            }
            return;
        }

        ArgumentSpec<?> spec = node.arguments().get(argIndex);
        RequiredArgumentBuilder<CommandSourceStack, ?> argBuilder = spec.type().createBrigadierArgument(spec.key().name());

        if (spec.suggestions() != null && spec.suggestions() != DynamicSuggestions.empty()) {
            argBuilder.suggests(spec.suggestions().toBrigadier());
        }

        if (spec.isOptional()) {
            builder.executes(ctx -> run(ctx, node, node.arguments().subList(0, argIndex)));
        }

        buildArgumentChain(argBuilder, node, argIndex + 1);
        builder.then(argBuilder);
    }

    private int run(CommandContext<CommandSourceStack> ctx, QuillCommand node, List<ArgumentSpec<?>> availableArgs) {
        try {
            SenderContext senderCtx = createSenderContext(ctx.getSource(), node.senderType());

            ArgumentMap.Builder argsBuilder = ArgumentMap.builder();
            for (ArgumentSpec<?> spec : availableArgs) {
                extractArgument(ctx, spec, argsBuilder);
            }

            for (int i = availableArgs.size(); i < node.arguments().size(); i++) {
                ArgumentSpec<?> spec = node.arguments().get(i);
                if (spec.isOptional()) {
                    @SuppressWarnings("unchecked")
                    ArgumentKey<Object> key = (ArgumentKey<Object>) spec.key();
                    argsBuilder.put(key, spec.defaultValue());
                }
            }

            ExecutionContext execCtx = ExecutionContextImpl.create(ctx, senderCtx, argsBuilder.build());
            CommandResult result = node.runner().run(execCtx);

            if (result instanceof CommandResult.Failure(String message)) {
                ctx.getSource().getSender().sendMessage(Component.text(message, NamedTextColor.RED));
                return 0;
            }

            return 1;

        } catch (IllegalStateException e) {
            Logger.warn("Command sender validation failed for '" + ctx.getInput() + "': " + e.getMessage());
            ctx.getSource().getSender().sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
            return 0;
        } catch (IllegalArgumentException e) {
            Logger.warn("Invalid argument in command '" + ctx.getInput() + "': " + e.getMessage());
            ctx.getSource().getSender().sendMessage(Component.text("Invalid argument: " + e.getMessage(), NamedTextColor.RED));
            return 0;
        } catch (CommandSyntaxException e) {
            Logger.warn("Command syntax error in '" + ctx.getInput() + "': " + e.getMessage());
            ctx.getSource().getSender().sendMessage(Component.text("Syntax error: " + e.getMessage(), NamedTextColor.RED));
            return 0;
        } catch (Exception e) {
            Logger.error("Unexpected error executing command '" + ctx.getInput() + "'", e);
            ctx.getSource().getSender().sendMessage(Component.text("An unexpected error occurred. Please contact an administrator.", NamedTextColor.RED));
            return 0;
        }
    }

    private SenderContext createSenderContext(CommandSourceStack source, QuillCommandBuilder.SenderType senderType) {
        return switch (senderType) {
            case PLAYER -> {
                if (!(source.getSender() instanceof Player player)) {
                    throw new IllegalStateException("This command can only be executed by players");
                }
                yield new SenderContext.PlayerOnly(player);
            }
            case CONSOLE -> {
                if (source.getSender() instanceof Player) {
                    throw new IllegalStateException("This command can only be executed from console");
                }
                yield new SenderContext.AnySender(source.getSender());
            }
            case ANY -> source.getSender() instanceof Player player
                    ? new SenderContext.PlayerOnly(player)
                    : new SenderContext.AnySender(source.getSender());
        };
    }

    @SuppressWarnings("unchecked")
    private <T> void extractArgument(CommandContext<CommandSourceStack> ctx, ArgumentSpec<T> spec, ArgumentMap.Builder builder) throws CommandSyntaxException {
        String name = spec.key().name();
        Object value = switch (spec.type()) {
            case ArgType.PlayerType ignored -> {
                PlayerSelectorArgumentResolver resolver = ctx.getArgument(name, PlayerSelectorArgumentResolver.class);
                List<Player> players = resolver.resolve(ctx.getSource());
                if (players.isEmpty()) throw new IllegalArgumentException("No player found");
                yield players.getFirst();
            }
            case ArgType.LocationType ignored -> {
                BlockPositionResolver resolver = ctx.getArgument(name, BlockPositionResolver.class);
                BlockPosition pos = resolver.resolve(ctx.getSource());
                yield Locations.of(ctx.getSource().getLocation().getWorld(), pos.blockX(), pos.blockY(), pos.blockZ());
            }
            default -> ctx.getArgument(name, spec.key().type());
        };

        ArgumentKey<Object> key = (ArgumentKey<Object>) spec.key();
        builder.put(key, value);
    }

    private boolean checkSenderType(CommandSourceStack source, QuillCommandBuilder.SenderType type) {
        return switch (type) {
            case PLAYER -> source.getSender() instanceof Player;
            case CONSOLE -> !(source.getSender() instanceof Player);
            case ANY -> true;
        };
    }
}