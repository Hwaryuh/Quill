package io.quill.paper.util.bukkit;

import io.quill.paper.Quill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.function.Supplier;

public final class Sudos {
    public static <T extends Event> T callEvent(Supplier<T> eventSupplier) {
        T event = eventSupplier.get();
        Quill.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static boolean runCommand(Player player, String command) {
        return player.performCommand(command);
    }

    public static void chat(Player player, String message) {
        player.chat(message);
    }
}