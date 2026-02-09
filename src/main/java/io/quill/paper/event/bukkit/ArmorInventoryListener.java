package io.quill.paper.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import static io.quill.paper.event.bukkit.ArmorEquipEventDispatcher.fireAndCheckCancel;
import static io.quill.paper.event.bukkit.ArmorEquipEventDispatcher.isAirOrNull;

final class ArmorInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.isCancelled() || e.getAction() == InventoryAction.NOTHING) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!isValidContext(e)) return;

        if (isShiftClick(e)) {
            onShiftClick(e, player);
        } else {
            onClick(e, player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent e) {
        ArmorType type = ArmorType.matchType(e.getOldCursor());
        if (type == null || e.getRawSlots().isEmpty()) return;

        int firstSlot = e.getRawSlots().stream().findFirst().orElse(-1);
        if (type.getSlot() == firstSlot) {
            if (fireAndCheckCancel((Player) e.getWhoClicked(), ArmorEquipEvent.EquipMethod.DRAG, type, null, e.getOldCursor())) {
                e.setResult(Event.Result.DENY);
                e.setCancelled(true);
            }
        }
    }

    private boolean isValidContext(InventoryClickEvent e) {
        InventoryType.SlotType slotType = e.getSlotType();
        if (slotType != InventoryType.SlotType.ARMOR && slotType != InventoryType.SlotType.QUICKBAR && slotType != InventoryType.SlotType.CONTAINER) {
            return false;
        }

        if (e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {
            return false;
        }

        InventoryType invType = e.getInventory().getType();
        return invType == InventoryType.CRAFTING || invType == InventoryType.PLAYER;
    }

    private boolean isShiftClick(InventoryClickEvent e) {
        ClickType click = e.getClick();
        return click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT;
    }

    private void onShiftClick(InventoryClickEvent e, Player player) {
        ArmorType type = ArmorType.matchType(e.getCurrentItem());
        if (type == null) return;

        boolean equipping = e.getRawSlot() != type.getSlot();
        if (equipping == type.isEmpty(player)) {
            ItemStack oldPiece = equipping ? null : e.getCurrentItem();
            ItemStack newPiece = equipping ? e.getCurrentItem() : null;

            if (fireAndCheckCancel(player, ArmorEquipEvent.EquipMethod.SHIFT_CLICK, type, oldPiece, newPiece)) {
                e.setCancelled(true);
            }
        }
    }

    private void onClick(InventoryClickEvent e, Player player) {
        boolean isNumberKey = e.getClick() == ClickType.NUMBER_KEY;

        ItemStack newArmorPiece;
        ItemStack oldArmorPiece = e.getCurrentItem();
        ArmorType type;

        if (isNumberKey && e.getClickedInventory() != null &&
                e.getClickedInventory().getType() == InventoryType.PLAYER) {

            ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
            if (!isAirOrNull(hotbarItem)) {
                type = ArmorType.matchType(hotbarItem);
                newArmorPiece = hotbarItem;
                oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
            } else {
                type = ArmorType.matchType(e.getCurrentItem());
                newArmorPiece = null;
            }
        } else if (isAirOrNull(e.getCursor()) && !isAirOrNull(e.getCurrentItem())) {
            type = ArmorType.matchType(e.getCurrentItem());
            newArmorPiece = null;
        } else {
            type = ArmorType.matchType(e.getCursor());
            newArmorPiece = e.getCursor();
        }

        if (type == null || e.getRawSlot() != type.getSlot()) return;

        ArmorEquipEvent.EquipMethod method = (e.getAction() == InventoryAction.HOTBAR_SWAP || isNumberKey)
                ? ArmorEquipEvent.EquipMethod.HOTBAR_SWAP
                : ArmorEquipEvent.EquipMethod.PICK_DROP;

        if (fireAndCheckCancel(player, method, type, oldArmorPiece, newArmorPiece)) {
            e.setCancelled(true);
        }
    }
}