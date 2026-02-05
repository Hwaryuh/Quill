package io.quill.paper.util.bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public final class Locations {
    private Locations() { }

    public static Location of(World world, double x, double y, double z) {
        return new Location(world, x, y, z);
    }

    public static Location of(World world, double x, double y, double z, float yaw, float pitch) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * 두 좌표 사이의 거리 계산 (Location 생성 없이)
     */
    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * 두 좌표 사이의 거리 제곱 계산 (sqrt 연산 제거)
     * 거리 비교만 필요할 때 사용
     */
    public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * 2D 거리 (y 좌표 무시)
     */
    public static double distance2D(double x1, double z1, double x2, double z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * 2D 거리 제곱
     */
    public static double distanceSquared2D(double x1, double z1, double x2, double z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return dx * dx + dz * dz;
    }

    /**
     * 좌표 이동 계산 (새 좌표 반환)
     */
    public static double[] add(double x, double y, double z, double offsetX, double offsetY, double offsetZ) {
        return new double[]{x + offsetX, y + offsetY, z + offsetZ};
    }

    /**
     * 중점 계산
     */
    public static double[] midpoint(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new double[]{
                (x1 + x2) / 2,
                (y1 + y2) / 2,
                (z1 + z2) / 2
        };
    }

    /**
     * Location 복사 (clone보다 빠름)
     */
    public static Location copy(Location loc) {
        return new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    /**
     * 중점 Location 생성
     */
    public static Location midpoint(Location loc1, Location loc2) {
        double[] mid = midpoint(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ());
        return new Location(loc1.getWorld(), mid[0], mid[1], mid[2]);
    }

    /**
     * 블록 중심 좌표
     */
    public static double[] toBlockCenter(double x, double y, double z) {
        return new double[]{
                Math.floor(x) + 0.5,
                Math.floor(y) + 0.5,
                Math.floor(z) + 0.5
        };
    }

    public static Location offset(Location loc, double x, double y, double z) {
        return new Location(
                loc.getWorld(),
                loc.getX() + x,
                loc.getY() + y,
                loc.getZ() + z
        );
    }

    public static Location offset(Location loc, Vector offset) {
        return new Location(
                loc.getWorld(),
                loc.getX() + offset.getX(),
                loc.getY() + offset.getY(),
                loc.getZ() + offset.getZ()
        );
    }

    /**
     * 좌표 배열을 Location으로 변환
     */
    public static Location toLocation(World world, double[] coords) {
        return new Location(world, coords[0], coords[1], coords[2]);
    }
}