package io.quill.paper.player;

import com.google.common.collect.Maps;
import io.quill.paper.Bootable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public final class PlayerContextManager implements Bootable {
    private static final PlayerContextManager INSTANCE = new PlayerContextManager();

    private final Map<UUID, PlayerContext> contexts = Maps.newHashMap();

    private PlayerContextManager() { }

    public static PlayerContextManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void start(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new CleanupListener(), plugin);
    }

    @Override
    public void end(JavaPlugin plugin) {
        contexts.clear();
    }

    public PlayerContext getOrCreate(Player player) {
        return contexts.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerContext(player));
    }

    void remove(UUID uuid) {
        PlayerContext ctx = contexts.remove(uuid);
        if (ctx != null) {
            ctx.clear();
        }
    }

    private class CleanupListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onQuit(PlayerQuitEvent e) {
            remove(e.getPlayer().getUniqueId());
        }
    }
}
