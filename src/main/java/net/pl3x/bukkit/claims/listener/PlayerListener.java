package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.player.task.WelcomeTask;
import org.bukkit.Material;
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
                if (player.isOnline()) {
                    pl3xPlayer.updateLocation(player.getLocation());
                }
            }
        }.runTaskLater(plugin, 20);

        // make sure everyone has at least the starting amount of claimblocks
        if (!player.hasPlayedBefore() || pl3xPlayer.getClaimBlocks() < 1) {
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

        plugin.getPlayerManager().getPlayer(event.getPlayer()).updateLocation(event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        plugin.getPlayerManager().getPlayer(event.getPlayer()).updateLocation(event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        plugin.getPlayerManager().getPlayer(event.getPlayer()).updateLocation(event.getPlayer().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPlaceFirstChest(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.CHEST) {
            return; // not placing a chest
        }

        if (plugin.getClaimManager().getClaim(event.getBlockPlaced().getLocation()) != null) {
            return; // already a claim here
        }

        Player player = event.getPlayer();
        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player);
        if (pl3xPlayer.getClaims().size() > 0) {
            return; // already has claims
        }

        Lang.send(player, Lang.USE_STICK_TO_CLAIM_THIS_LAND);
    }
}
