package io.quill.paper.menu;

import io.quill.paper.Quill;
import io.quill.paper.Bootable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MenuListener implements Bootable, Listener {
    @Override
    public void start(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void end(JavaPlugin plugin) {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        InventoryMenu menu = MenuManager.getInstance().getMenu(player.getUniqueId());
        if (menu == null) return;

        if (e.getClickedInventory() == menu.getInventory()) {
            menu.onClick(e);
        } else if (e.getClickedInventory() == player.getInventory()) {
            menu.onPlayerInventoryClick(e);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;

        InventoryMenu menu = MenuManager.getInstance().getMenu(player.getUniqueId());
        if (menu == null) return;

        if (e.getInventory() != menu.getInventory()) return;

        if (!menu.canClose(e)) {
            var plugin = Quill.getInstance().getPlugin();
            plugin.getServer().getScheduler().runTask(plugin, menu::reopen);
            return;
        }

        menu.onClose(e);
        MenuManager.getInstance().unregister(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        InventoryMenu menu = MenuManager.getInstance().getMenu(player.getUniqueId());
        if (menu == null) return;

        if (menu.shouldCancelDrag(e)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        MenuManager.getInstance().unregister(e.getPlayer().getUniqueId());
    }
}