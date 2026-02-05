package io.quill.paper.menu.slot;

import io.quill.paper.menu.button.InventoryButton;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

// 조건에 따라 버튼이 동적으로 바뀌는 슬롯
public class StatefulSlot implements InventoryButton {
    private final Supplier<InventoryButton> buttonSupplier;

    public StatefulSlot(Supplier<InventoryButton> buttonSupplier) {
        this.buttonSupplier = buttonSupplier;
    }

    @Override
    public ItemStack getIcon() {
        return buttonSupplier.get().getIcon();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        buttonSupplier.get().onClick(event);
    }

    @Override
    public boolean shouldCancel(InventoryClickEvent event) {
        return buttonSupplier.get().shouldCancel(event);
    }
}