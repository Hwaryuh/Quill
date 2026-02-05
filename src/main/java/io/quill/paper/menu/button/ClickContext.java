package io.quill.paper.menu.button;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public record ClickContext(InventoryClickEvent event) {

    // cursor
    public boolean hasCursor(Material material) {
        ItemStack cursor = event.getCursor();
        return cursor.getType() == material;
    }

    public boolean isEmptyCursor() {
        return event.getCursor().isEmpty();
    }

    public ItemStack getCursor() {
        return event.getCursor();
    }

    // slots
    public boolean hasSlotItem(Material material) {
        ItemStack current = event.getCurrentItem();
        return current != null && current.getType() == material;
    }

    public boolean isEmptySlot() {
        ItemStack current = event.getCurrentItem();
        return current == null || current.getType().isAir();
    }

    public ItemStack getSlotItem() {
        return event.getCurrentItem();
    }

    // Action
    public boolean isAction(InventoryAction... actions) {
        for (InventoryAction action : actions) {
            if (event.getAction() == action) return true;
        }
        return false;
    }

    public InventoryAction getAction() {
        return event.getAction();
    }

    // helper
    public boolean isPickup() {
        return isAction(
                InventoryAction.PICKUP_ALL,
                InventoryAction.PICKUP_SOME,
                InventoryAction.PICKUP_HALF,
                InventoryAction.PICKUP_ONE
        );
    }

    public boolean isPlace() {
        return isAction(
                InventoryAction.PLACE_ALL,
                InventoryAction.PLACE_SOME,
                InventoryAction.PLACE_ONE
        );
    }

    public boolean isSwap() {
        return isAction(InventoryAction.SWAP_WITH_CURSOR);
    }

    public boolean isMove() {
        return isAction(InventoryAction.MOVE_TO_OTHER_INVENTORY);
    }

    public boolean isHotbarSwap() {
        return isAction(InventoryAction.HOTBAR_SWAP);
    }
}