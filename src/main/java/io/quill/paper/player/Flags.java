package io.quill.paper.player;

import com.google.common.collect.Sets;

import java.util.Set;

final class Flags {
    private final Set<String> data = Sets.newHashSet();

    boolean has(String key) {
        return data.contains(key);
    }

    void set(String key) {
        data.add(key);
    }

    void remove(String key) {
        data.remove(key);
    }

    void toggle(String key) {
        if (has(key)) {
            remove(key);
        } else {
            set(key);
        }
    }
}