package io.quill.paper.item.require;

import com.google.common.collect.ImmutableList;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;

@Immutable
final class AnyRequirement implements InventoryRequirement {
    private final ImmutableList<InventoryRequirement> requirements;

    AnyRequirement(ImmutableList<InventoryRequirement> requirements) {
        checkArgument(!requirements.isEmpty(), "requirements must not be empty");
        this.requirements = requirements;
    }

    @Override
    public boolean test(PlayerInventory inventory) {
        return requirements.stream().anyMatch(req -> req.test(inventory));
    }

    @Override
    public ConsumeResult tryConsume(PlayerInventory inventory) {
        if (!test(inventory)) {
            return ConsumeResult.failure("No requirement satisfied");
        }

        for (InventoryRequirement requirement : requirements) {
            if (requirement.test(inventory)) {
                return requirement.tryConsume(inventory);
            }
        }

        return ConsumeResult.failure("No requirement can be consumed");
    }
}