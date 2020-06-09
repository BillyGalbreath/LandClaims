package net.pl3x.bukkit.claims.visualization;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class VisualizationElement {
    private Location location;
    private final Material material;
    private final byte data;
    private final BlockData blockData;

    public VisualizationElement(Location location, Material material, byte data) {
        this(location, material, data, material.createBlockData());
    }

    public VisualizationElement(Location location, Material material, byte data, BlockData blockData) {
        this.location = location;
        this.material = material;
        this.data = data;
        this.blockData = blockData;
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

    public BlockData getBlockData() {
        return blockData;
    }
}
