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

    public static InventoryRequirement requireLegacyModelData(Material material, int customModelData, int amount) {
        return new LegacyModelDataRequirement(material, customModelData, amount);
    }

    public static InventoryRequirement helmet(Material material) {
        return ArmorRequirement.helmet(material);
    }

    public static InventoryRequirement chestplate(Material material) {
        return ArmorRequirement.chestplate(material);
    }

    public static InventoryRequirement leggings(Material material) {
        return ArmorRequirement.leggings(material);
    }

    public static InventoryRequirement boots(Material material) {
        return ArmorRequirement.boots(material);
    }

    public static InventoryRequirement helmet(ItemStack item) {
        return ArmorRequirement.helmet(item);
    }

    public static InventoryRequirement chestplate(ItemStack item) {
        return ArmorRequirement.chestplate(item);
    }

    public static InventoryRequirement leggings(ItemStack item) {
        return ArmorRequirement.leggings(item);
    }

    public static InventoryRequirement boots(ItemStack item) {
        return ArmorRequirement.boots(item);
    }

    public static InventoryRequirement fullArmorSet(ArmorMaterial material) {
        String prefix = material.name();
        return all(
                helmet(Material.valueOf(prefix + "_HELMET")),
                chestplate(Material.valueOf(prefix + "_CHESTPLATE")),
                leggings(Material.valueOf(prefix + "_LEGGINGS")),
                boots(Material.valueOf(prefix + "_BOOTS"))
        );
    }
}