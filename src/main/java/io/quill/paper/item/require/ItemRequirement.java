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

    /**
     * ItemStack 기반 요구사항 생성
     * NBT, enchantment, durability 등 모든 메타데이터를 비교한다
     */
    static ItemRequirement of(ItemStack item, int amount) {
        return new ItemRequirement(item.clone(), amount);
    }

    /**
     * Material 기반 요구사항 생성
     * 메타데이터는 비교하지 않고 Material만 일치하면 된다
     */
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
            return ConsumeResult.failure(
                    String.format("Insufficient items: need %d, have %d", amount, countMatching(inventory))
            );
        }

        int remaining = amount;
        ItemStack[] contents = inventory.getStorageContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null || !stack.isSimilar(template)) {
                continue;
            }

            int toRemove = Math.min(stack.getAmount(), remaining);
            stack.setAmount(stack.getAmount() - toRemove);
            remaining -= toRemove;

            if (remaining <= 0) {
                return ConsumeResult.success();
            }
        }

        return remaining <= 0
                ? ConsumeResult.success()
                : ConsumeResult.failure("Failed to consume items");
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