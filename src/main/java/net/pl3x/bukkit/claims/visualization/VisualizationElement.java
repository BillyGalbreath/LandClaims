package net.pl3x.bukkit.claims.visualization;

import org.bukkit.Location;
import org.bukkit.Material;

public class VisualizationElement {
    private Location location;
    private final Material material;
    private final byte data;

    public VisualizationElement(Location location, Material material, byte data) {
        this.location = location;
        this.material = material;
        this.data = data;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getMaterial() {
        return material;
    }

    public byte getData() {
        return data;
    }
}
