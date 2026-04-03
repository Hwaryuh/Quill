package io.quill.paper.util.bukkit.pdc;

import com.google.common.collect.Maps;
import io.quill.paper.Quill;
import org.bukkit.NamespacedKey;

import java.util.Map;

public final class PDCKeys {
    private static final String DEFAULT_NAMESPACE = Quill.getInstance().getPlugin().getName().toLowerCase();
    private static final Map<String, NamespacedKey> CACHE = Maps.newConcurrentMap();

    public static NamespacedKey of(String key) {
        return of(DEFAULT_NAMESPACE, key);
    }

    public static NamespacedKey of(String namespace, String key) {
        String cacheKey = namespace + ":" + key;
        return CACHE.computeIfAbsent(cacheKey, k -> new NamespacedKey(namespace, key));
    }

    private PDCKeys() { }
}