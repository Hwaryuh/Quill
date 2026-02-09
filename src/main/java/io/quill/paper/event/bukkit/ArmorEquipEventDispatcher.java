package io.quill.paper.event.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

final class ArmorEquipEventDispatcher {
    private ArmorEquipEventDispatcher() { }

    static boolean fireAndCheckCancel(Player player, ArmorEquipEvent.EquipMethod method, ArmorType type, ItemStack oldPiece, ItemStack newPiece) {
        ArmorEquipEvent event = new ArmorEquipEvent(player, method, type, oldPiece, newPiece);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType() == org.bukkit.Material.AIR;
    }
}