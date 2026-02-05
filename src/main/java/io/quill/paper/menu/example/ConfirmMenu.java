package io.quill.paper.menu.example;

import io.quill.paper.menu.button.ButtonBuilder;
import io.quill.paper.menu.InventoryMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

@SuppressWarnings("UnstableApiUsage")
public class ConfirmMenu extends InventoryMenu {
    private boolean selected = false;

    public ConfirmMenu(Player player) {
        super(player, MenuType.GENERIC_9X3, Component.text("정말 삭제하시겠습니까?"));
    }

    @Override
    protected void render() {
        setButton(11, new ButtonBuilder()
                .icon(ItemStack.of(Material.GREEN_WOOL))
                .onLeftClick(ctx -> {
                    selected = true;
                    player.sendMessage("삭제되었습니다.");
                    close();
                })
                .build()
        );

        setButton(15, new ButtonBuilder()
                .icon(ItemStack.of(Material.RED_WOOL))
                .onLeftClick(ctx -> {
                    selected = true;
                    player.sendMessage("취소되었습니다.");
                    close();
                })
                .build()
        );

        setButton(0, new ButtonBuilder()
                .icon(null)
                .onLeftClick(e -> setTitle(Component.text("테스트", NamedTextColor.YELLOW)))
                .build()
        );
    }

    @Override
    protected boolean canClose(InventoryCloseEvent event) {
        if (!selected) {
            player.sendMessage("확인 또는 취소 버튼을 선택해주세요!");
            return false;
        }
        return true;
    }
}