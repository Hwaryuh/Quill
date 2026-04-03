package io.quill.paper.util.bukkit;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class Selectors {

    /**
     * 특정 위치에서 가장 가까운 엔티티를 찾는다.
     */
    public static <T extends Entity> T nearest(
            Location location,
            Class<T> entityClass,
            double maxDistance,
            Predicate<T> condition
    ) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(
                location,
                maxDistance,
                maxDistance,
                maxDistance
        );

        T nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity entity : nearbyEntities) {
            if (!entityClass.isInstance(entity)) continue;

            @SuppressWarnings("unchecked")
            T typedEntity = (T) entity;

            if (condition != null && !condition.test(typedEntity)) continue;

            double distance = location.distance(entity.getLocation());
            if (distance < nearestDistance) {
                nearest = typedEntity;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    /**
     * 특정 엔티티에서 가장 가까운 엔티티를 찾는다 (자기 자신 제외).
     */
    public static <T extends Entity> T nearest(
            Entity source,
            Class<T> entityClass,
            double maxDistance,
            Predicate<T> condition
    ) {
        if (source == null) return null;

        Location location = source.getLocation();
        if (location.getWorld() == null) return null;

        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(
                location,
                maxDistance,
                maxDistance,
                maxDistance
        );

        T nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity entity : nearbyEntities) {
            if (entity.equals(source)) continue;
            if (!entityClass.isInstance(entity)) continue;

            @SuppressWarnings("unchecked")
            T typedEntity = (T) entity;

            if (condition != null && !condition.test(typedEntity)) continue;

            double distance = location.distance(entity.getLocation());
            if (distance < nearestDistance) {
                nearest = typedEntity;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    /**
     * 특정 범위 내 모든 조건을 만족하는 엔티티를 찾는다.
     */
    public static <T extends Entity> List<T> findAll(
            Location location,
            Class<T> entityClass,
            double maxDistance,
            Predicate<T> condition
    ) {
        if (location == null || location.getWorld() == null) {
            return Lists.newArrayList();
        }

        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(
                location,
                maxDistance,
                maxDistance,
                maxDistance
        );

        List<T> result = Lists.newArrayList();

        for (Entity entity : nearbyEntities) {
            if (!entityClass.isInstance(entity)) continue;

            @SuppressWarnings("unchecked")
            T typedEntity = (T) entity;

            if (condition == null || condition.test(typedEntity)) {
                result.add(typedEntity);
            }
        }

        return result;
    }
}