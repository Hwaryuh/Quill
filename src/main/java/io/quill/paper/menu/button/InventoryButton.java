package io.quill.paper.menu.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface InventoryButton {
    ItemStack getIcon();

    void onClick(InventoryClickEvent event);

    default boolean shouldCancel(InventoryClickEvent event) {
        return true;
    }
}