package io.quill.paper.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public sealed interface SenderContext permits SenderContext.AnySender, SenderContext.PlayerOnly {

    record AnySender(CommandSender sender) implements SenderContext {
        public Optional<Player> asPlayer() {
            return sender instanceof Player p ? Optional.of(p) : Optional.empty();
        }
    }

    record PlayerOnly(Player player) implements SenderContext {
        public CommandSender sender() {
            return player;
        }
    }
}