package io.quill.paper.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;

@SuppressWarnings("UnstableApiUsage")
record ExecutionContextImpl<S extends SenderContext>(
        S sender,
        ArgumentMap arguments,
        String fullCommand
) implements ExecutionContext<S> {

    static <S extends SenderContext> ExecutionContextImpl<S> create(
            CommandContext<CommandSourceStack> ctx,
            S senderCtx,
            ArgumentMap args
    ) {
        return new ExecutionContextImpl<>(senderCtx, args, ctx.getInput());
    }
}