package net.pl3x.bukkit.cities.claim;

import net.pl3x.bukkit.cities.claim.region.Coordinates;
import net.pl3x.bukkit.cities.claim.region.Region;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class City extends Region {
    private final String name;
    private final Set<Plot> plots = new HashSet<>();

    public City(long id, String name, UUID owner, Coordinates coordinates) {
        super(id, owner, coordinates);
        this.name = name;
    }

    public City(UUID owner, String name, Coordinates coordinates) {
        this(getNextId(), name, owner, coordinates);
    }

    public String getName() {
        return name;
    }

    public Set<Plot> getPlots() {
        return plots;
    }

    public Plot getPlot(Location location) {
        for (Plot plot : plots) {
            if (plot.contains(location)) {
                return plot;
            }
        }
        return null;
    }

    public void unload() {
        plots.forEach(Plot::stopVisuals);
        plots.clear();
        stopVisuals();
    }
}
