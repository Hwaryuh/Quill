package io.quill.paper.item.require;

import javax.annotation.concurrent.Immutable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

@Immutable
final class LegacyModelDataRequirement implements InventoryRequirement {
    private final Material material;
    private final int customModelData;
    private final int amount;

    LegacyModelDataRequirement(Material material, int customModelData, int amount) {
        this.material = material;
        this.customModelData = customModelData;
        this.amount = amount;
    }

    @Override
    public boolean test(PlayerInventory inventory) {
        return countMatching(inventory) >= amount;
    }

    @Override
    public ConsumeResult tryConsume(PlayerInventory inventory) {
        if (!test(inventory)) {
            return ConsumeResult.failure("Insufficient items");
        }

        InventorySnapshot snapshot = InventorySnapshot.capture(inventory);

        int remaining = amount;
        ItemStack[] contents = inventory.getStorageContents();

        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (!matches(stack)) continue;

            int toRemove = Math.min(stack.getAmount(), remaining);
            stack.setAmount(stack.getAmount() - toRemove);
            remaining -= toRemove;
        }

        if (remaining > 0) {
            snapshot.restore(inventory);
            return ConsumeResult.failure("Failed to consume items");
        }

        return ConsumeResult.success();
    }

    private boolean matches(ItemStack stack) {
        if (stack == null || stack.getType() != material) return false;

        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == customModelData;
    }

    private int countMatching(PlayerInventory inventory) {
        int count = 0;
        for (ItemStack stack : inventory.getStorageContents()) {
            if (matches(stack)) {
                count += stack.getAmount();
            }
        }
        return count;
    }
}