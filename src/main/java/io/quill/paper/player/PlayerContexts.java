package io.quill.paper.player;

import org.bukkit.entity.Player;

public final class PlayerContexts {
    private PlayerContexts() { }

    public static PlayerContext ctx(Player player) {
        return PlayerContextManager.getInstance().getOrCreate(player);
    }
}
