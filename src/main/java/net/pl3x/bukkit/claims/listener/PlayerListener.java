package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.player.task.WelcomeTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
    private final Pl3xClaims plugin;

    public PlayerListener(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player); // load player data
        new BukkitRunnable() {
            @Override
            public void run() {
                // wait 20 ticks to update location for spawn relocation
                pl3xPlayer.updateLocation();
            }
        }.runTaskLater(plugin, 20);

        if (!player.hasPlayedBefore()) {
            pl3xPlayer.setClaimBlocks(Config.STARTING_BLOCKS);
            new WelcomeTask(player).runTaskLater(plugin, 200L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerManager().unload(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockX() == event.getFrom().getBlockX()
                && event.getTo().getBlockY() == event.getFrom().getBlockY()
                && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
            return; // did not move a full block
        }

        plugin.getPlayerManager().getPlayer(event.getPlayer()).updateLocation();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        plugin.getPlayerManager().getPlayer(event.getPlayer()).updateLocation();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        plugin.getPlayerManager().getPlayer(event.getPlayer()).updateLocation();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPlaceFirstChest(BlockPlaceEvent event) {
        //
        // TODO
        //
    }
}
