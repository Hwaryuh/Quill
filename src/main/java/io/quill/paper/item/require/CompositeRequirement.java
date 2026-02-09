package io.quill.paper.item.require;

import org.bukkit.inventory.PlayerInventory;

import javax.annotation.concurrent.Immutable;

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
        InventorySnapshot snapshot = InventorySnapshot.capture(inventory);

        ConsumeResult leftResult = left.tryConsume(inventory);
        if (!leftResult.isSuccess()) {
            snapshot.restore(inventory);
            return ConsumeResult.failure("Left requirement consume failed: " + leftResult.reason());
        }

        ConsumeResult rightResult = right.tryConsume(inventory);
        if (!rightResult.isSuccess()) {
            snapshot.restore(inventory);
            return ConsumeResult.failure("Right requirement consume failed: " + rightResult.reason());
        }

        return ConsumeResult.success();
    }

    private ConsumeResult consumeOr(PlayerInventory inventory) {
        if (left.test(inventory)) {
            return left.tryConsume(inventory);
        }

        return right.tryConsume(inventory);
    }
}