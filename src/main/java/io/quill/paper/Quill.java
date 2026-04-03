package io.quill.paper;

import com.google.common.collect.Lists;
import io.quill.paper.event.EventManager;
import io.quill.paper.event.bukkit.ArmorEquipEventListener;
import io.quill.paper.menu.MenuListener;
import io.quill.paper.menu.MenuManager;
import io.quill.paper.player.PlayerContextManager;
import io.quill.paper.util.bukkit.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Quill {
    private static Quill instance;
    private JavaPlugin plugin;
    private final List<Bootable> bootables = Lists.newArrayList();

    private Quill() { }

    public static void initialize(JavaPlugin plugin, Bootable... extra) {
        if (instance != null) throw new IllegalStateException("Already initialized");

        instance = new Quill();
        instance.plugin = plugin;

        EventManager.initialize(plugin);
        MenuManager.initialize();

        instance.bootables.add(PlayerContextManager.getInstance());
        instance.bootables.add(new MenuListener());
        instance.bootables.add(new ArmorEquipEventListener());

        instance.bootables.addAll(List.of(extra));

        for (Bootable b : instance.bootables) {
            b.start(plugin);
        }
    }

    public static void shutdown(JavaPlugin plugin) {
        if (instance == null) return;

        for (Bootable b : Lists.reverse(instance.bootables)) {
            if (b == null) continue;
            try {
                b.end(plugin);
            } catch (Exception e) {
                Logger.error("Error during shutdown in " + b.getClass().getSimpleName(), e);
            }
        }

        try { MenuManager.shutdown(); }
        catch (Exception e) { Logger.error("Error during MenuManager shutdown", e); }

        try { EventManager.shutdown(); }
        catch (Exception e) { Logger.error("Error during EventManager shutdown", e); }

        instance = null;
    }

    public static Quill getInstance() {
        return instance;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}