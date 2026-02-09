package io.quill.paper.item.require;

import com.google.common.collect.ImmutableList;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;

@Immutable
final class AllRequirement implements InventoryRequirement {
    private final ImmutableList<InventoryRequirement> requirements;

    AllRequirement(ImmutableList<InventoryRequirement> requirements) {
        checkArgument(!requirements.isEmpty(), "requirements must not be empty");
        this.requirements = requirements;
    }

    @Override
    public boolean test(PlayerInventory inventory) {
        return requirements.stream().allMatch(req -> req.test(inventory));
    }

    @Override
    public ConsumeResult tryConsume(PlayerInventory inventory) {
        if (!test(inventory)) {
            return ConsumeResult.failure("Not all requirements satisfied");
        }

        InventorySnapshot snapshot = InventorySnapshot.capture(inventory);

        for (int i = 0; i < requirements.size(); i++) {
            ConsumeResult result = requirements.get(i).tryConsume(inventory);
            if (!result.isSuccess()) {
                snapshot.restore(inventory);
                return ConsumeResult.failure(
                        String.format("Requirement %d failed: %s", i, result.reason())
                );
            }
        }

        return ConsumeResult.success();
    }
}