package io.quill.paper.item.require;

import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static com.google.common.base.Preconditions.checkArgument;

public final class InventoryRequirements {
    private InventoryRequirements() { }

    public static InventoryRequirement require(Material material, int amount) {
        return ItemRequirement.of(material, amount);
    }

    public static InventoryRequirement require(ItemStack item, int amount) {
        return ItemRequirement.of(item, amount);
    }

    public static InventoryRequirement all(InventoryRequirement... requirements) {
        checkArgument(requirements.length > 0, "requirements must not be empty");
        return new AllRequirement(ImmutableList.copyOf(requirements));
    }

    public static InventoryRequirement any(InventoryRequirement... requirements) {
        checkArgument(requirements.length > 0, "requirements must not be empty");
        return new AnyRequirement(ImmutableList.copyOf(requirements));
    }
}