package io.quill.paper.menu;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;

public final class MenuManager {
    private static MenuManager instance;
    private final Map<UUID, InventoryMenu> openMenus = Maps.newHashMap();

    private MenuManager() { }

    public static void initialize() {
        checkState(instance == null, "MenuManager already initialized");
        instance = new MenuManager();
    }

    public static void shutdown() {
        checkInitialized();
        instance.openMenus.clear();
        instance = null;
    }

    private static void checkInitialized() {
        checkState(instance != null, "MenuManager not initialized. Call MenuManager.initialize() first.");
    }

    public static MenuManager getInstance() {
        checkInitialized();
        return instance;
    }

    public void register(InventoryMenu menu) {
        openMenus.put(menu.getPlayer().getUniqueId(), menu);
    }

    public void unregister(UUID playerId) {
        openMenus.remove(playerId);
    }

    public InventoryMenu getMenu(UUID playerId) {
        return openMenus.get(playerId);
    }
}