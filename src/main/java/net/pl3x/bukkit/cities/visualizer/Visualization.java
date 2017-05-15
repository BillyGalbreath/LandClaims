package net.pl3x.bukkit.cities.visualizer;

import net.pl3x.bukkit.cities.claim.City;
import net.pl3x.bukkit.cities.claim.region.Region;
import net.pl3x.bukkit.cities.visualizer.particle.Color;
import net.pl3x.bukkit.cities.visualizer.particle.Particle;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class Visualization {
    private final Collection<Particle> particles = new HashSet<>();

    public Visualization(Region region) {
        Location min = region.getMinLocation();
        Location max = region.getMaxLocation();

        Color fill = region instanceof City ? Color.BLUE : Color.GREEN;
        Color edge = region instanceof City ? Color.YELLOW : Color.WHITE_SMOKE;

        double minX = Math.min(min.getX(), max.getX());
        double minY = Math.min(min.getY(), max.getY());
        double minZ = Math.min(min.getZ(), max.getZ());
        double maxX = Math.max(min.getX(), max.getX()) + 1; // add 1 to compensate for block size
        double maxY = Math.max(min.getY(), max.getY()) + 1;
        double maxZ = Math.max(min.getZ(), max.getZ()) + 1;

        for (double y = minY; y <= maxY; y++) {
            for (double x = minX; x <= maxX; x++) {
                Color color = x == minX || x == maxX || y == minY || y == maxY ? fill : edge;
                particles.add(new Particle(new Location(min.getWorld(), x, y, minZ), color)); // north
                particles.add(new Particle(new Location(min.getWorld(), x, y, maxZ), color)); // south
            }
            for (double z = minZ + 1; z < maxZ; z++) {
                Color color = y == minY || y == maxY ? fill : edge;
                particles.add(new Particle(new Location(min.getWorld(), minX, y, z), color)); // west
                particles.add(new Particle(new Location(min.getWorld(), maxX, y, z), color)); // east
            }
        }
    }

    void sendPackets(Player watcher) {
        particles.forEach(particle -> particle.sendPacket(watcher));
    }
}
