package io.quill.paper.menu.internal;

import io.quill.paper.menu.button.ClickContext;
import io.quill.paper.menu.button.DynamicButton;
import io.quill.paper.menu.button.InventoryButton;
import io.quill.paper.menu.slot.ShiftClickRouter;
import io.quill.paper.util.bukkit.Logger;
import io.quill.paper.util.bukkit.task.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

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

                        event.setCancelled(true);
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
        Logger.info("=== tryRouteToSlots 시작 ===");
        Logger.info("아이템: " + item.getType() + " x" + item.getAmount());
        Logger.info("대상 슬롯들: " + Arrays.toString(slots));

        for (int slot : slots) {
            Logger.info("슬롯 " + slot + " 처리 중... 남은 수량: " + item.getAmount());

            ItemStack currentSlotItem = registry.buttons.getInventory().getItem(slot);
            Logger.info("  -> 슬롯 내용: " + (currentSlotItem != null ? currentSlotItem.getType() + " x" + currentSlotItem.getAmount() : "empty"));

            InventoryButton button = registry.buttons.getButton(slot);
            Logger.info("  -> 버튼 타입: " + (button != null ? button.getClass().getSimpleName() : "null"));

            if (button instanceof AdvancedSlotFilterButton filterButton) {
                Logger.info("  -> AdvancedSlotFilterButton 발견");
                boolean result = filterButton.getFilter().handleShiftClick(item, registry.buttons.getInventory(), slot);
                Logger.info("  -> handleShiftClick 결과: " + result + ", 남은 수량: " + item.getAmount());

                if (result) {
                    Logger.info("  -> 성공! 즉시 종료");
                    return true;
                }
            }
        }

        Logger.info("=== tryRouteToSlots 종료: 모두 실패, 최종 수량=" + item.getAmount() + " ===");
        return false;
    }
}