package net.pl3x.bukkit.claims.claim;

import net.pl3x.bukkit.claims.claim.flag.Flags;
import net.pl3x.bukkit.claims.claim.group.Groups;
import org.bukkit.Location;

import java.util.UUID;

/**
 * 2D Claim (X,Z)
 */
public class Claim {
    private static long nextId = 0;

    public static long getNextId() {
        return nextId++;
    }

    private final long id;
    private final long parent;
    private final Coordinates coordinates;
    private final Flags flags;
    private final Groups groups;

    public Claim(long id, long parent, UUID owner, Coordinates coordinates) {
        this.id = id;
        this.parent = parent;
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

    public long getParentId() {
        return parent;
    }

    public Claim getParent() {
        return ClaimManager.getInstance().getClaim(parent);
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
     * Check if a claim overlaps this claim at any location
     *
     * @param claim Claim to check
     * @return True if any location overlaps this claim
     */
    public boolean overlaps(Claim claim) {
        return coordinates.overlaps(claim.getCoordinates());
    }

    /**
     * Check if a claim fits completely inside this claim
     *
     * @param claim Claim to check
     * @return True if claim fits completely inside this claim
     */
    public boolean contains(Claim claim) {
        return coordinates.contains(claim.getCoordinates());
    }

    public boolean contains(Location location) {
        return coordinates.contains(location);
    }

    /**
     * Check if a specific point is inside this claim
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return True is point is inside this claim
     */
    public boolean contains(int x, int z) {
        return coordinates.contains(x, z);
    }
}
