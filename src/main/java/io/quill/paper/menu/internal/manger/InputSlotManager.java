package io.quill.paper.menu.internal.manger;

import java.util.HashSet;
import java.util.Set;

public class InputSlotManager {
    private final Set<Integer> inputSlots = new HashSet<>();

    public void register(int slot) {
        inputSlots.add(slot);
    }

    public void unregister(int slot) {
        inputSlots.remove(slot);
    }

    public Set<Integer> getInputSlots() {
        return inputSlots;
    }

    public boolean isInputSlot(int slot) {
        return inputSlots.contains(slot);
    }
}