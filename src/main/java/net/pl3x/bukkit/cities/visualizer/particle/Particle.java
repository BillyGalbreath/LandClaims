package net.pl3x.bukkit.cities.visualizer.particle;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class Particle {
    private final Location location;
    private final PacketContainer packet;

    public Particle(Location location, Color color) {
        this.location = location;
        this.packet = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.WORLD_PARTICLES);

        packet.getParticles()
                .write(0, EnumWrappers.Particle.REDSTONE);
        packet.getIntegers()
                .write(0, 0);                      // amount (always 0)
        packet.getBooleans()
                .write(0, true);                   // long distance
        packet.getFloat()
                .write(0, (float) location.getX()) // x position
                .write(1, (float) location.getY()) // y position
                .write(2, (float) location.getZ()) // z position
                .write(3, color.getRed())          // x offset (red)
                .write(4, color.getGreen())        // y offset (green)
                .write(5, color.getBlue())         // z offset (blue)
                .write(6, 1F);                     // data (speed)
    }

    public void sendPacket(Player watcher) {
        if (!location.getWorld().equals(watcher.getLocation().getWorld())) {
            return; // not even close
        }
        if (location.distanceSquared(watcher.getLocation()) > 4096) {
            return; // farther than 64 blocks away
        }

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(watcher, packet);
        } catch (InvocationTargetException ignore) {
        }
    }
}
