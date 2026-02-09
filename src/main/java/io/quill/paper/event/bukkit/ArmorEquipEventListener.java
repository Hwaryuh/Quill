package io.quill.paper.event.bukkit;

import io.quill.paper.Bootable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ArmorEquipEventListener implements Bootable {
    private final Listener[] listeners = {
            new ArmorInventoryListener(),
            new ArmorInteractListener()
    };

    @Override
    public void start(JavaPlugin plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pm.registerEvents(listener, plugin);
        }
    }

    @Override
    public void end(JavaPlugin plugin) {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }
}