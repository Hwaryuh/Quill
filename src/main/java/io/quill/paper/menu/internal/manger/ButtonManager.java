package io.quill.paper.menu.internal.manger;

import com.google.common.collect.Maps;
import io.quill.paper.menu.button.DynamicButton;
import io.quill.paper.menu.button.InventoryButton;
import io.quill.paper.menu.internal.AdvancedSlotFilterButton;
import io.quill.paper.menu.slot.AdvancedSlotFilter;
import io.quill.paper.menu.slot.PlaceholderSlot;
import io.quill.paper.menu.slot.SlotFilter;
import io.quill.paper.menu.slot.StatefulSlot;
import io.quill.paper.util.bukkit.task.Tasks;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ButtonManager {
    private final Inventory inventory;
    private final Map<Integer, InventoryButton> buttons = Maps.newHashMap();

    public ButtonManager(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setButton(int slot, InventoryButton button) {
        buttons.put(slot, button);
        if (button.getIcon() != null) {
            setItem(slot, button.getIcon());
        }
    }

    public void removeButton(int slot) {
        buttons.remove(slot);
        setSlotEmpty(slot);
    }

    public void removeButtons(int... slots) {
        for (int slot : slots) {
            removeButton(slot);
        }
    }

    public InventoryButton getButton(int slot) {
        return buttons.get(slot);
    }

    public void setItem(int slot, ItemStack item) {
        Tasks.run(() -> inventory.setItem(slot, item));
    }

    public void setSlotEmpty(int slot) {
        this.setItem(slot, ItemStack.of(Material.AIR));
    }

    public void setSlotEmpty(int... slots) {
        for (int slot : slots) {
            this.setItem(slot, ItemStack.of(Material.AIR));
        }
    }

    public void setSlotFilter(int slot, Predicate<ItemStack> filter) {
        setAdvancedSlotFilter(slot, SlotFilter.of(filter));
    }

    public void setSlotFilter(int slot, SlotFilter filter) {
        setAdvancedSlotFilter(slot, filter);
    }

    public void setAdvancedSlotFilter(int slot, AdvancedSlotFilter filter) {
        buttons.put(slot, new AdvancedSlotFilterButton(filter, inventory));
    }

    public void setPlaceholderSlot(int slot, ItemStack placeholder, Predicate<ItemStack> itemFilter, Integer maxAmount, Runnable onPlaced) {
        setPlaceholderSlot(slot, placeholder, itemFilter, maxAmount, onPlaced, null);
    }

    public void setPlaceholderSlot(
            int slot,
            ItemStack placeholder,
            Predicate<ItemStack> itemFilter,
            Integer maxAmount,
            Runnable onPlaced,
            Runnable onPickup
    ) {
        PlaceholderSlot placeholderSlot = new PlaceholderSlot(placeholder, itemFilter, inventory, slot, maxAmount);
        if (onPlaced != null) placeholderSlot.onPlaced(onPlaced);
        if (onPickup != null) placeholderSlot.onPickup(onPickup);
        buttons.put(slot, new AdvancedSlotFilterButton(placeholderSlot, inventory));
        placeholderSlot.showPlaceholder();
    }

    public void setDynamicButton(int slot, DynamicButton button) {
        buttons.put(slot, button);
    }

    public void setStatefulSlot(int slot, Supplier<InventoryButton> buttonSupplier) {
        buttons.put(slot, new StatefulSlot(buttonSupplier));
    }

    public Inventory getInventory() {
        return inventory;
    }
}
