package io.quill.paper.menu.internal.manger;

import com.google.common.collect.Maps;
import io.quill.paper.menu.button.ButtonBuilder;
import io.quill.paper.menu.button.InventoryButton;

import java.util.Map;
import java.util.function.Consumer;

public class PlayerInventoryManager {
    private final Map<Integer, InventoryButton> playerButtons = Maps.newHashMap();
    private InventoryButton fallback = null;

    public void setButton(int slot, InventoryButton button) {
        playerButtons.put(slot, button);
    }

    public InventoryButton getButton(int slot) {
        return playerButtons.get(slot);
    }

    public void setFallback(Consumer<ButtonBuilder> configurator) {
        ButtonBuilder builder = new ButtonBuilder();
        configurator.accept(builder);
        this.fallback = builder.build();
    }

    public InventoryButton getFallback() {
        return fallback;
    }
}