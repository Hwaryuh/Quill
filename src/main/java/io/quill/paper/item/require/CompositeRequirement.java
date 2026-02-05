package io.quill.paper.item.require;

import com.google.common.collect.ImmutableMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.concurrent.Immutable;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
final class CompositeRequirement implements InventoryRequirement {
    enum Type { AND, OR }

    private final InventoryRequirement left;
    private final InventoryRequirement right;
    private final Type type;

    CompositeRequirement(InventoryRequirement left, InventoryRequirement right, Type type) {
        this.left = checkNotNull(left);
        this.right = checkNotNull(right);
        this.type = checkNotNull(type);
    }

    @Override
    public boolean test(PlayerInventory inventory) {
        return type == Type.AND
                ? left.test(inventory) && right.test(inventory)
                : left.test(inventory) || right.test(inventory);
    }

    @Override
    public ConsumeResult tryConsume(PlayerInventory inventory) {
        if (!test(inventory)) {
            return ConsumeResult.failure("Requirements not satisfied");
        }

        return type == Type.AND ? consumeAnd(inventory) : consumeOr(inventory);
    }

    private ConsumeResult consumeAnd(PlayerInventory inventory) {
        Map<Integer, ItemStack> snapshot = captureSnapshot(inventory);

        ConsumeResult leftResult = left.tryConsume(inventory);
        if (!leftResult.isSuccess()) {
            restoreSnapshot(inventory, snapshot);
            return ConsumeResult.failure("Left requirement consume failed: " + leftResult.reason());
        }

        ConsumeResult rightResult = right.tryConsume(inventory);
        if (!rightResult.isSuccess()) {
            restoreSnapshot(inventory, snapshot);
            return ConsumeResult.failure("Right requirement consume failed: " + rightResult.reason());
        }

        return ConsumeResult.success();
    }

    private ConsumeResult consumeOr(PlayerInventory inventory) {
        if (left.test(inventory)) {
            return left.tryConsume(inventory);
        }

        if (right.test(inventory)) {
            return right.tryConsume(inventory);
        }

        return ConsumeResult.failure("Neither requirement can be consumed");
    }

    private Map<Integer, ItemStack> captureSnapshot(PlayerInventory inventory) {
        ImmutableMap.Builder<Integer, ItemStack> builder = ImmutableMap.builder();
        ItemStack[] contents = inventory.getStorageContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack != null) {
                builder.put(i, stack.clone());
            }
        }

        return builder.build();
    }

    private void restoreSnapshot(PlayerInventory inventory, Map<Integer, ItemStack> snapshot) {
        ItemStack[] contents = inventory.getStorageContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack restored = snapshot.get(i);
            contents[i] = restored != null ? restored.clone() : null;
        }

        inventory.setStorageContents(contents);
    }
}