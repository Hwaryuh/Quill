package io.quill.paper.item.require;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
final class ArmorRequirement implements InventoryRequirement {
    enum Slot {
        HELMET(39),
        CHESTPLATE(38),
        LEGGINGS(37),
        BOOTS(36);

        final int index;

        Slot(int index) {
            this.index = index;
        }
    }

    private final ItemStack template;
    private final Slot slot;

    private ArmorRequirement(ItemStack template, Slot slot) {
        this.template = checkNotNull(template, "template");
        this.slot = checkNotNull(slot, "slot");
    }

    static ArmorRequirement helmet(Material material) {
        return new ArmorRequirement(ItemStack.of(material), Slot.HELMET);
    }

    static ArmorRequirement chestplate(Material material) {
        return new ArmorRequirement(ItemStack.of(material), Slot.CHESTPLATE);
    }

    static ArmorRequirement leggings(Material material) {
        return new ArmorRequirement(ItemStack.of(material), Slot.LEGGINGS);
    }

    static ArmorRequirement boots(Material material) {
        return new ArmorRequirement(ItemStack.of(material), Slot.BOOTS);
    }

    static ArmorRequirement helmet(ItemStack item) {
        return new ArmorRequirement(item.clone(), Slot.HELMET);
    }

    static ArmorRequirement chestplate(ItemStack item) {
        return new ArmorRequirement(item.clone(), Slot.CHESTPLATE);
    }

    static ArmorRequirement leggings(ItemStack item) {
        return new ArmorRequirement(item.clone(), Slot.LEGGINGS);
    }

    static ArmorRequirement boots(ItemStack item) {
        return new ArmorRequirement(item.clone(), Slot.BOOTS);
    }

    @Override
    public boolean test(PlayerInventory inventory) {
        ItemStack worn = inventory.getItem(slot.index);
        return worn != null && worn.getType() == template.getType();
    }

    @Override
    public ConsumeResult tryConsume(PlayerInventory inventory) {
        if (!test(inventory)) {
            return ConsumeResult.failure(String.format("Required armor not worn in %s slot", slot.name().toLowerCase()));
        }

        return ConsumeResult.success();
    }
}
