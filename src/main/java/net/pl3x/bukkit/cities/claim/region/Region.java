package net.pl3x.bukkit.cities.claim.region;

import net.pl3x.bukkit.cities.claim.region.flag.Flags;
import net.pl3x.bukkit.cities.claim.region.group.Groups;
import org.bukkit.Location;

import java.util.UUID;

/**
 * 2D Region (X,Z)
 */
public abstract class Region {
    private static long nextId = 0;

    public static long getNextId() {
        return nextId++;
    }

    private final long id;
    private final Coordinates coordinates;
    private final Flags flags;
    private final Groups groups;

    public Region(long id, UUID owner, Coordinates coordinates) {
        this.id = id;
        this.coordinates = coordinates;

        flags = new Flags();
        groups = new Groups(owner);

        // fix next id in case we ended up skipping ids when loading from disk
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    public long getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Flags getFlags() {
        return flags;
    }

    public Groups getGroups() {
        return groups;
    }

    /**
     * Get the farthest most northwest and bottom location
     *
     * @return Most northwest and bottom location
     */
    public Location getMinLocation() {
        return coordinates.getMinLocation();
    }

    /**
     * Get the farthest most southeast and top location
     *
     * @return Most southeast and top location
     */
    public Location getMaxLocation() {
        return coordinates.getMaxLocation();
    }

    /**
     * Check if a region overlaps this region at any location
     * <p>
     * Useful to make sure a city/plot doesnt overlap another city/plot
     *
     * @param region Region to check
     * @return True if any location overlaps this region
     */
    public boolean overlaps(Region region) {
        return coordinates.overlaps(region.getCoordinates());
    }

    /**
     * Check if a region fits completely inside this region
     * <p>
     * Useful to see of a plot fits inside a city
     *
     * @param region Region to check
     * @return True if region fits completely inside this region
     */
    public boolean contains(Region region) {
        return coordinates.contains(region.getCoordinates());
    }

    public boolean contains(Location location) {
        return coordinates.contains(location);
    }

    /**
     * Check if a specific point is inside this region
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return True is point is inside this region
     */
    public boolean contains(int x, int z) {
        return coordinates.contains(x, z);
    }
}
