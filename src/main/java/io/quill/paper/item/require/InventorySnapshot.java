package io.quill.paper.item.require;

import com.google.common.collect.ImmutableMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.concurrent.Immutable;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
final class InventorySnapshot {
    private final ImmutableMap<Integer, ItemStack> storage;
    private final ImmutableMap<Integer, ItemStack> armor;
    private final ItemStack offhand;

    private InventorySnapshot(ImmutableMap<Integer, ItemStack> storage, ImmutableMap<Integer, ItemStack> armor, ItemStack offhand) {
        this.storage = storage;
        this.armor = armor;
        this.offhand = offhand;
    }

    static InventorySnapshot capture(PlayerInventory inventory) {
        checkNotNull(inventory, "inventory");
        return new InventorySnapshot(
                captureContents(inventory.getStorageContents()),
                captureContents(inventory.getArmorContents()),
                cloneNullable(inventory.getItemInOffHand())
        );
    }

    void restore(PlayerInventory inventory) {
        restoreContents(inventory.getStorageContents(), storage, inventory::setStorageContents);
        restoreContents(inventory.getArmorContents(), armor, inventory::setArmorContents);
        inventory.setItemInOffHand(cloneNullable(offhand));
    }

    private static ImmutableMap<Integer, ItemStack> captureContents(ItemStack[] contents) {
        ImmutableMap.Builder<Integer, ItemStack> builder = ImmutableMap.builder();
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack != null) {
                builder.put(i, stack.clone());
            }
        }
        return builder.build();
    }

    private static void restoreContents(ItemStack[] current, ImmutableMap<Integer, ItemStack> snapshot, Consumer<ItemStack[]> setter) {
        for (int i = 0; i < current.length; i++) {
            ItemStack restored = snapshot.get(i);
            current[i] = cloneNullable(restored);
        }
        setter.accept(current);
    }

    private static ItemStack cloneNullable(ItemStack stack) {
        return stack != null ? stack.clone() : null;
    }
}