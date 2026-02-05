package io.quill.paper.menu.internal;

import io.quill.paper.menu.button.ClickContext;
import io.quill.paper.menu.button.InventoryButton;
import io.quill.paper.menu.slot.AdvancedSlotFilter;
import io.quill.paper.menu.slot.PlaceholderSlot;
import io.quill.paper.util.bukkit.task.Tasks;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdvancedSlotFilterButton implements InventoryButton {
    private final AdvancedSlotFilter filter;
    private final Inventory inventory;

    public AdvancedSlotFilterButton(AdvancedSlotFilter filter, Inventory inventory) {
        this.filter = filter;
        this.inventory = inventory;
    }

    public AdvancedSlotFilter getFilter() {
        return filter;
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public void onClick(InventoryClickEvent event) {}

    @Override
    public boolean shouldCancel(InventoryClickEvent event) {
        ClickContext ctx = new ClickContext(event);

        if (ctx.isPickup()) {
            ItemStack slotItem = ctx.getSlotItem();
            if (slotItem == null) return false;

            AdvancedSlotFilter.PickupResult result = filter.handlePickup(slotItem, ctx);

            if (result == AdvancedSlotFilter.PickupResult.ALLOW && filter instanceof PlaceholderSlot placeholder) {
                Tasks.run(() -> {
                    ItemStack current = inventory.getItem(event.getSlot());
                    if (current == null || current.getType().isAir()) {
                        placeholder.showPlaceholder();
                    }
                });
            }

            return result != AdvancedSlotFilter.PickupResult.ALLOW;
        }

        if (ctx.isPlace()) {
            ItemStack cursor = ctx.getCursor();
            AdvancedSlotFilter.PlaceResult result = filter.handlePlace(cursor, ctx);
            return result != AdvancedSlotFilter.PlaceResult.ALLOW;
        }

        if (ctx.isSwap()) {
            ItemStack cursor = ctx.getCursor();
            AdvancedSlotFilter.PlaceResult result = filter.handlePlace(cursor, ctx);
            return result != AdvancedSlotFilter.PlaceResult.ALLOW;
        }

        if (ctx.isMove()) {
            ItemStack slotItem = ctx.getSlotItem();
            if (slotItem == null) return false;

            AdvancedSlotFilter.PickupResult result = filter.handlePickup(slotItem, ctx);

            if (result == AdvancedSlotFilter.PickupResult.ALLOW && filter instanceof PlaceholderSlot placeholder) {
                Tasks.run(() -> {
                    ItemStack current = inventory.getItem(event.getSlot());
                    if (current == null || current.getType().isAir()) {
                        placeholder.showPlaceholder();
                    }
                });
            }

            return result != AdvancedSlotFilter.PickupResult.ALLOW;
        }

        if (ctx.isHotbarSwap()) {
            ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());

            if (hotbarItem == null || hotbarItem.getType().isAir()) {
                ItemStack slotItem = ctx.getSlotItem();
                if (slotItem == null) return false;

                AdvancedSlotFilter.PickupResult result = filter.handlePickup(slotItem, ctx);

                if (result == AdvancedSlotFilter.PickupResult.ALLOW && filter instanceof PlaceholderSlot placeholder) {
                    Tasks.run(() -> {
                        ItemStack current = inventory.getItem(event.getSlot());
                        if (current == null || current.getType().isAir()) {
                            placeholder.showPlaceholder();
                        }
                    });
                }

                return result != AdvancedSlotFilter.PickupResult.ALLOW;
            }

            AdvancedSlotFilter.PlaceResult result = filter.handlePlace(hotbarItem, ctx);
            return result != AdvancedSlotFilter.PlaceResult.ALLOW;
        }

        return true;
    }
}