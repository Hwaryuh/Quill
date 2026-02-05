package io.quill.paper.menu.internal.manger;

import com.google.common.collect.Lists;
import io.quill.paper.menu.slot.ShiftClickRouter;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public class ShiftClickManager {
    private final List<ShiftClickRouter> routers = Lists.newArrayList();

    public void addRouter(Predicate<ItemStack> matcher, IntSupplier... suppliers) {
        routers.add(new ShiftClickRouter(matcher, suppliers));
    }

    public void addRouter(Predicate<ItemStack> matcher, int... staticSlots) {
        IntSupplier[] suppliers = Arrays.stream(staticSlots)
                .mapToObj(slot -> (IntSupplier) () -> slot)
                .toArray(IntSupplier[]::new);
        addRouter(matcher, suppliers);
    }

    public List<ShiftClickRouter> getRouters() {
        return routers;
    }
}