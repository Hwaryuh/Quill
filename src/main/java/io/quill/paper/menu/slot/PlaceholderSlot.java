package io.quill.paper.menu.slot;

import io.quill.paper.menu.button.ClickContext;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

// 슬롯이 비어있을 때 플레이스홀더 아이템을 표시하고, 아이템 배치 시 자동으로 제거
public class PlaceholderSlot implements AdvancedSlotFilter {
    private final ItemStack placeholder;
    private final Predicate<ItemStack> itemFilter;
    private final Inventory inventory;
    private final int slot;
    private final Integer maxAmount;

    public PlaceholderSlot(ItemStack placeholder, Predicate<ItemStack> itemFilter, Inventory inventory, int slot, Integer maxAmount) {
        this.placeholder = placeholder;
        this.itemFilter = itemFilter;
        this.inventory = inventory;
        this.slot = slot;
        this.maxAmount = maxAmount;
    }

    @Override
    public PlaceResult handlePlace(ItemStack item, ClickContext ctx) {
        if (!itemFilter.test(item)) return PlaceResult.DENY;

        ItemStack slotItem = ctx.getSlotItem();
        boolean hasPlaceholder = isPlaceholder(slotItem);

        if (!hasPlaceholder && slotItem != null && !slotItem.getType().isAir()) {
            if (mergeStack(slotItem, item)) {
                return PlaceResult.HANDLED;
            }
            return PlaceResult.DENY;
        }

        PlacementResult result = placeItemInSlot(item, slotItem, inventory, slot);
        return result.success ? PlaceResult.HANDLED : PlaceResult.DENY;
    }

    @Override
    public PickupResult handlePickup(ItemStack item, ClickContext ctx) {
        if (isPlaceholder(item)) {
            return PickupResult.DENY;
        }
        return PickupResult.ALLOW;
    }

    @Override
    public boolean handleShiftClick(ItemStack clicked, Inventory targetInventory, int slot) {
        if (!itemFilter.test(clicked)) return false;

        ItemStack slotItem = targetInventory.getItem(slot);

        if (slotItem != null && !slotItem.getType().isAir() && !isPlaceholder(slotItem)) {
            return mergeStack(slotItem, clicked);
        }

        PlacementResult result = placeItemInSlot(clicked, slotItem, targetInventory, slot);
        return result.success;
    }

    private boolean mergeStack(ItemStack slotItem, ItemStack incomingItem) {
        if (!slotItem.isSimilar(incomingItem)) return false;
        if (maxAmount == null) return false;

        int currentAmount = slotItem.getAmount();
        int space = maxAmount - currentAmount;

        if (space <= 0) return false;

        int toAdd = Math.min(space, incomingItem.getAmount());
        slotItem.setAmount(currentAmount + toAdd);
        incomingItem.setAmount(incomingItem.getAmount() - toAdd);

        return true;
    }

    private PlacementResult placeItemInSlot(ItemStack item, ItemStack currentSlot, Inventory targetInv, int targetSlot) {
        if (isPlaceholder(currentSlot)) {
            targetInv.setItem(targetSlot, ItemStack.empty());
        }

        int amount = maxAmount != null ? Math.min(maxAmount, item.getAmount()) : item.getAmount();

        ItemStack toPlace = item.clone();
        toPlace.setAmount(amount);
        targetInv.setItem(targetSlot, toPlace);

        item.setAmount(item.getAmount() - amount);

        return new PlacementResult(true, amount);
    }

    public boolean isPlaceholder(ItemStack item) {
        return item != null && item.isSimilar(placeholder);
    }

    public void showPlaceholder() {
        ItemStack current = inventory.getItem(slot);
        if (current == null || current.getType().isAir()) {
            inventory.setItem(slot, placeholder);
        }
    }

    public void hidePlaceholder() {
        ItemStack current = inventory.getItem(slot);
        if (isPlaceholder(current)) {
            inventory.setItem(slot, ItemStack.empty());
        }
    }

    private record PlacementResult(boolean success, int placed) { }
}