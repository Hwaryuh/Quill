package io.quill.paper.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;

@SuppressWarnings("UnstableApiUsage")
record ExecutionContextImpl(SenderContext sender, ArgumentMap arguments, String fullCommand) implements ExecutionContext {
    static ExecutionContextImpl create(CommandContext<CommandSourceStack> ctx, SenderContext senderCtx, ArgumentMap args) {
        return new ExecutionContextImpl(senderCtx, args, ctx.getInput());
    }
}