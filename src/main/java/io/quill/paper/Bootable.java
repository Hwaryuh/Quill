package io.quill.paper;

import org.bukkit.plugin.java.JavaPlugin;

public interface Bootable {
    void start(JavaPlugin plugin);

    void end(JavaPlugin plugin);
}