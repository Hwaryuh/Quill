package io.quill.paper.item.require;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
final class ItemRequirement implements InventoryRequirement {
    private final ItemStack template;
    private final int amount;

    private ItemRequirement(ItemStack template, int amount) {
        this.template = checkNotNull(template, "template");
        checkArgument(amount > 0, "amount must be positive: %s", amount);
        this.amount = amount;
    }

    static ItemRequirement of(ItemStack item, int amount) {
        return new ItemRequirement(item.clone(), amount);
    }

    static ItemRequirement of(Material material, int amount) {
        return new ItemRequirement(new ItemStack(material), amount);
    }

    @Override
    public boolean test(PlayerInventory inventory) {
        return countMatching(inventory) >= amount;
    }

    @Override
    public ConsumeResult tryConsume(PlayerInventory inventory) {
        if (!test(inventory)) {
            return ConsumeResult.failure(String.format("Insufficient items: need %d, have %d", amount, countMatching(inventory)));
        }

        InventorySnapshot snapshot = InventorySnapshot.capture(inventory);

        int remaining = amount;
        ItemStack[] contents = inventory.getStorageContents();

        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack == null || !stack.isSimilar(template)) {
                continue;
            }

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

    private int countMatching(PlayerInventory inventory) {
        int count = 0;
        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack != null && stack.isSimilar(template)) {
                count += stack.getAmount();
            }
        }
        return count;
    }
}