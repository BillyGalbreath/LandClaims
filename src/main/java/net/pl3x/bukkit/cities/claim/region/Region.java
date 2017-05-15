package net.pl3x.bukkit.cities.claim.region;

import net.pl3x.bukkit.cities.Pl3xCities;
import net.pl3x.bukkit.cities.claim.region.group.RegionGroups;
import net.pl3x.bukkit.cities.visualizer.Visualization;
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
    private final RegionFlags flags;
    private final RegionGroups groups;
    private final Visualization visualization;

    public Region(long id, UUID owner, Coordinates coordinates) {
        this.id = id;
        this.coordinates = coordinates;

        flags = new RegionFlags();
        groups = new RegionGroups(owner);

        // fix next id in case we ended up skipping ids when loading from disk
        if (id >= nextId) {
            nextId = id + 1;
        }

        visualization = new Visualization(this);
    }

    public long getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public RegionFlags getFlags() {
        return flags;
    }

    public RegionGroups getGroups() {
        return groups;
    }

    public Location getMinLocation() {
        return coordinates.getMinLocation();
    }

    public Location getMaxLocation() {
        return coordinates.getMaxLocation();
    }

    public boolean overlaps(Region region) {
        return coordinates.overlaps(region.getCoordinates());
    }

    public boolean contains(Region region) {
        return coordinates.contains(region.getCoordinates());
    }

    public boolean contains(Location location) {
        return coordinates.contains(location);
    }

    public boolean contains(int x, int z) {
        return coordinates.contains(x, z);
    }

    public void stopVisuals() {
        Pl3xCities.getPlugin().getVisualizationTask()
                .removeVisualization(visualization);
    }
}
