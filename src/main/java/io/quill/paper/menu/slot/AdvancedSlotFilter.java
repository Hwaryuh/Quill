package io.quill.paper.menu.slot;

import io.quill.paper.menu.button.ClickContext;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface AdvancedSlotFilter {
    /**
     * 아이템 배치 시도 처리
     * @return 처리 결과
     */
    PlaceResult handlePlace(ItemStack item, ClickContext context);

    /**
     * 아이템 회수 시도 처리
     * @return 처리 결과
     */
    default PickupResult handlePickup(ItemStack item, ClickContext context) {
        return PickupResult.ALLOW;
    }

    /**
     * Shift-Click 처리 (선택적)
     */
    default boolean handleShiftClick(ItemStack clicked, Inventory targetInventory, int slot) {
        return false;
    }

    enum PlaceResult {
        ALLOW,
        DENY,
        HANDLED
    }

    enum PickupResult {
        ALLOW,
        DENY,
        HANDLED
    }
}