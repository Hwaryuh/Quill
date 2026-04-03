package io.quill.paper.menu.internal;

import io.quill.paper.menu.button.ClickContext;
import io.quill.paper.menu.button.DynamicButton;
import io.quill.paper.menu.button.InventoryButton;
import io.quill.paper.menu.slot.ShiftClickRouter;
import io.quill.paper.util.bukkit.task.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClickHandler {
    private final ButtonRegistry registry;
    private final Player player;

    public ClickHandler(ButtonRegistry registry, Player player) {
        this.registry = registry;
        this.player = player;
    }

    public void handle(InventoryClickEvent event) {
        InventoryButton button = registry.buttons.getButton(event.getRawSlot());
        if (button != null) {
            if (button.shouldCancel(event)) {
                event.setCancelled(true);
            }

            if (button instanceof DynamicButton dynamicButton) {
                if (event.isCancelled()) {
                    ClickContext ctx = new ClickContext(event);
                    ItemStack newIcon = dynamicButton.createIcon(ctx);
                    registry.buttons.setItem(event.getRawSlot(), newIcon);
                }
            }

            button.onClick(event);
            Tasks.run(player::updateInventory);
        } else {
            event.setCancelled(true);
        }
    }

    public void handlePlayerInventory(InventoryClickEvent event) {
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && !clicked.getType().isAir()) {
                for (ShiftClickRouter router : registry.shiftClicks.getRouters()) {
                    if (router.matches(clicked)) {
                        if (tryRouteToSlots(clicked, router.getTargetSlots())) {
                            event.setCancelled(true);
                            return;
                        }

                        event.setCancelled(true); // <-- 항상 실행됨 (routeToSlots 성공/실패 무관)
                        return;
                    }
                }
            }
        }

        InventoryButton button = registry.playerInventory.getButton(event.getSlot());
        if (button != null) {
            if (button.shouldCancel(event)) {
                event.setCancelled(true);
            }
            button.onClick(event);
        } else {
            InventoryButton fallback = registry.playerInventory.getFallback();
            if (fallback != null) {
                if (fallback.shouldCancel(event)) {
                    event.setCancelled(true);
                }
                fallback.onClick(event);
            }
        }
    }

    private boolean tryRouteToSlots(ItemStack item, int[] slots) {
        for (int slot : slots) {
            InventoryButton button = registry.buttons.getButton(slot);
            if (button instanceof AdvancedSlotFilterButton filterButton) {
                boolean result = filterButton.getFilter().handleShiftClick(item, registry.buttons.getInventory(), slot);
                if (result) {
                    return true;
                }
            }
        }

        return false;
    }
}
