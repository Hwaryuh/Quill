package io.quill.paper.item.require;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.concurrent.Immutable;

import java.util.Map;

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

        Map<Integer, ItemStack> snapshot = captureSnapshot(inventory);

        for (int i = 0; i < requirements.size(); i++) {
            ConsumeResult result = requirements.get(i).tryConsume(inventory);
            if (result.isSuccess() == false) {
                restoreSnapshot(inventory, snapshot);
                return ConsumeResult.failure(
                        String.format("Requirement %d failed: %s", i, result.reason())
                );
            }
        }

        return ConsumeResult.success();
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