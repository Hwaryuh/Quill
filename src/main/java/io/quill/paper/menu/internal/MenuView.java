package io.quill.paper.menu.internal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class MenuView {
    private final Player player;
    private final MenuType.Typed<?, ?> menuType;
    private Component currentTitle;
    private InventoryView view;

    public MenuView(Player player, MenuType.Typed<?, ?> menuType, Component title) {
        this.player = player;
        this.menuType = menuType;
        this.currentTitle = title;
        this.view = createView();
    }

    @SuppressWarnings("unchecked")
    private InventoryView createView() {
        return ((MenuType.Typed<@NotNull InventoryView, @NotNull InventoryViewBuilder<@NotNull InventoryView>>) menuType)
                .builder()
                .title(currentTitle)
                .build(player);
    }

    public InventoryView getView() {
        return view;
    }

    public Inventory getInventory() {
        return view.getTopInventory();
    }

    @SuppressWarnings("deprecation")
    public void setTitle(Component title) {
        this.currentTitle = title;
        view.setTitle(LegacyComponentSerializer.legacySection().serialize(title));
    }

    public void reopen() {
        ItemStack[] contents = getInventory().getContents();
        this.view = createView();

        Inventory newInv = getInventory();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                newInv.setItem(i, contents[i]);
            }
        }

        view.open();
    }
}