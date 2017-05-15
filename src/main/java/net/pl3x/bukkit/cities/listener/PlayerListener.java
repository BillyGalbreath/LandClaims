package net.pl3x.bukkit.cities.listener;

import net.pl3x.bukkit.cities.Pl3xCities;
import net.pl3x.bukkit.cities.player.Pl3xPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
    private final Pl3xCities plugin;

    public PlayerListener(Pl3xCities plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Pl3xPlayer pl3xPlayer = Pl3xPlayer.getPlayer(event.getPlayer()); // load player data
        new BukkitRunnable() {
            @Override
            public void run() {
                pl3xPlayer.updateLocation(); // wait 20 ticks to update location for spawn relocation
            }
        }.runTaskLater(plugin, 20);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Pl3xPlayer.unload(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockX() == event.getFrom().getBlockX()
                && event.getTo().getBlockY() == event.getFrom().getBlockY()
                && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
            return; // did not move a full block
        }

        Pl3xPlayer.getPlayer(event.getPlayer()).updateLocation();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Pl3xPlayer.getPlayer(event.getPlayer()).updateLocation();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Pl3xPlayer.getPlayer(event.getPlayer()).updateLocation();
    }
}
