package io.quill.paper.menu.slot;

import io.quill.paper.menu.button.ClickContext;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public interface SlotFilter extends AdvancedSlotFilter {
    boolean canPlace(ItemStack item, InventoryClickEvent event);

    default boolean canPickup(ItemStack item, InventoryClickEvent event) {
        return true;
    }

    default Integer getMaxPlaceAmount() {
        return null;
    }

    default void onPlaced() { }

    @Override
    default PlaceResult handlePlace(ItemStack item, ClickContext context) {
        if (!canPlace(item, context.event())) {
            return PlaceResult.DENY;
        }

        ItemStack slotItem = context.getSlotItem();
        if (slotItem != null && !slotItem.getType().isAir()) {
            return PlaceResult.DENY;
        }

        Integer maxAmount = getMaxPlaceAmount();
        if (maxAmount != null && item.getAmount() > maxAmount) {
            ItemStack toPlace = item.clone();
            toPlace.setAmount(maxAmount);
            context.event().getClickedInventory().setItem(context.event().getSlot(), toPlace);
            item.setAmount(item.getAmount() - maxAmount);
            onPlaced();
            return PlaceResult.HANDLED;
        }

        onPlaced();
        return PlaceResult.ALLOW;
    }

    @Override
    default PickupResult handlePickup(ItemStack item, ClickContext context) {
        return canPickup(item, context.event()) ? PickupResult.ALLOW : PickupResult.DENY;
    }

    @Override
    default boolean handleShiftClick(ItemStack clicked, Inventory targetInventory, int slot) {
        if (!canPlace(clicked, null)) return false;

        ItemStack slotItem = targetInventory.getItem(slot);
        if (slotItem != null && !slotItem.getType().isAir()) return false;

        Integer maxAmount = getMaxPlaceAmount();
        int amount = maxAmount != null ? Math.min(maxAmount, clicked.getAmount()) : 1;

        ItemStack toPlace = clicked.clone();
        toPlace.setAmount(amount);
        targetInventory.setItem(slot, toPlace);
        clicked.setAmount(clicked.getAmount() - amount);

        this.onPlaced();
        return true;
    }

    static SlotFilter of(Predicate<ItemStack> predicate) {
        return (item, event) -> predicate.test(item);
    }
}