package io.quill.paper.menu.example;

import io.quill.paper.menu.InventoryMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

@SuppressWarnings("UnstableApiUsage")
public class SimpleTestMenu extends InventoryMenu {
    public SimpleTestMenu(Player player) {
        super(player, MenuType.GENERIC_9X6, Component.text("테스트 메뉴", NamedTextColor.BLUE));
    }

    @Override
    protected void render() {
        // 예시 1
        setButton(2, button(ItemStack.of(Material.EMERALD))
                .onLeftClick(ctx -> {
                    if (ctx.isEmptyCursor() && ctx.isAction(InventoryAction.PICKUP_ALL)) {
                        player.sendMessage("에메랄드 픽업!");
                    }
                })
                .cancelNone()
                .build()
        );

        // 예시 2
        setButton(12, button()
                .onLeftClick(ctx -> {
                    if (ctx.hasCursor(Material.DIAMOND)) {
                        player.sendMessage("다이아 배치!");
                    } else if (ctx.hasCursor(Material.GOLD_INGOT)) {
                        player.sendMessage("금 배치!");
                    } else if (ctx.isEmptyCursor()) {
                        player.sendMessage("빈 손 클릭!");
                    }
                })
                .cancelIf(ctx -> {
                    if (!ctx.isEmptySlot()) return true;
                    return !ctx.hasCursor(Material.DIAMOND) && !ctx.hasCursor(Material.GOLD_INGOT);
                })
                .build()
        );

        // 예시 3
        setButton(20, button(ItemStack.of(Material.IRON_INGOT))
                .onLeftClick(ctx -> {
                    if (ctx.hasCursor(Material.DIAMOND) &&
                            !ctx.isAction(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                        player.sendMessage("다이아를 들고 철 클릭!");
                    }
                })
                .build()
        );

        // 플레이어 인벤토리
        setPlayerInventoryButton(0, button()
                .onLeftClick(ctx -> {
                    if (ctx.hasSlotItem(Material.DIAMOND)) {
                        player.sendMessage("다이아 클릭!");
                    }
                })
                .build()
        );
    }
}