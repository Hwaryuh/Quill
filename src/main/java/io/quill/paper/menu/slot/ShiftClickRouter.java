package io.quill.paper.menu.slot;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public class ShiftClickRouter {
    private final Predicate<ItemStack> matcher;
    private final IntSupplier[] targetSlotSuppliers;

    public ShiftClickRouter(Predicate<ItemStack> matcher, IntSupplier... targetSlotSuppliers) {
        this.matcher = matcher;
        this.targetSlotSuppliers = targetSlotSuppliers;
    }

    public boolean matches(ItemStack item) {
        return matcher.test(item);
    }

    public int[] getTargetSlots() {
        return Arrays.stream(targetSlotSuppliers)
                .mapToInt(IntSupplier::getAsInt)
                .filter(slot -> slot >= 0)
                .toArray();
    }
}