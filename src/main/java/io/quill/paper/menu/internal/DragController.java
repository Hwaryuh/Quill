package io.quill.paper.menu.internal;

import io.quill.paper.menu.DragPolicy;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Set;
import java.util.function.Predicate;

public class DragController {
    private DragPolicy policy = DragPolicy.PREVENT_ALL;
    private Predicate<Set<Integer>> customChecker = null;

    public void setPolicy(DragPolicy policy) {
        this.policy = policy;
    }

    public void setCustomChecker(Predicate<Set<Integer>> checker) {
        this.policy = DragPolicy.CUSTOM;
        this.customChecker = checker;
    }

    public boolean shouldCancel(InventoryDragEvent event, int topInventorySize) {
        return switch (policy) {
            case ALLOW_ALL -> false;
            case PREVENT_ALL -> true;
            case PREVENT_TOP_ONLY -> {
                for (int slot : event.getRawSlots()) {
                    if (slot < topInventorySize) {
                        yield true;
                    }
                }
                yield false;
            }
            case CUSTOM -> customChecker == null || !customChecker.test(event.getRawSlots());
        };
    }
}