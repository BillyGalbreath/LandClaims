package net.pl3x.bukkit.claims.claim;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashSet;

public class Coordinates {
    private final World world;
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;

    public Coordinates(Location location1, Location location2) {
        this(location1.getWorld(), (int) location1.getX(), (int) location1.getZ(), (int) location2.getX(), (int) location2.getZ());
    }

    public Coordinates(World world, int x1, int z1, int x2, int z2) {
        this.world = world;

        minX = Math.min(x1, x2);
        minZ = Math.min(z1, z2);
        maxX = Math.max(x1, x2);
        maxZ = Math.max(z1, z2);
    }

    public World getWorld() {
        return world;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public Location getMinLocation() {
        return new Location(world, minX, 0, minZ);
    }

    public Location getMaxLocation() {
        return new Location(world, maxX, world.getMaxHeight(), maxZ);
    }

    public boolean overlaps(Coordinates coordinates) {
        if (!coordinates.getWorld().equals(world)) {
            return false; // not same world
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (coordinates.contains(x, z)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(Coordinates coordinates) {
        if (!coordinates.getWorld().equals(world)) {
            return false; // not same world
        }

        for (int x = coordinates.minX; x <= coordinates.maxX; x++) {
            for (int z = coordinates.minZ; z <= coordinates.maxZ; z++) {
                if (!contains(x, z)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean contains(Location location) {
        return location.getWorld().equals(world) && contains(location.getBlockX(), location.getBlockZ());
    }

    public boolean contains(int x, int z) {
        return x >= minX && x <= maxX &&
                z >= minZ && z <= maxZ;
    }

    public Collection<Long> getChunkHashes() {
        Collection<Long> hashes = new HashSet<>();
        for (int x = minX >> 4; x <= maxX >> 4; x++) {
            for (int z = minZ >> 4; z <= maxZ >> 4; z++) {
                hashes.add(ClaimManager.getInstance().getChunkHash(x, z));
            }
        }
        return hashes;
    }
}
