package net.pl3x.bukkit.claims.player.task;

import net.pl3x.bukkit.claims.Logger;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.ClaimManager;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTrappedPlayerTask extends BukkitRunnable {
    private final Pl3xPlayer pl3xPlayer;
    private final Location original;
    private Location destination;
    private Logger logger;

    public SaveTrappedPlayerTask(Pl3xPlayer pl3xPlayer, Location original, Location destination) {
        this.pl3xPlayer = pl3xPlayer;
        this.original = original;
        this.destination = destination;
        this.logger = pl3xPlayer.getPlugin().getLog();
    }

    @Override
    public void run() {
        Player player = pl3xPlayer.getPlayer().getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        if (player.getLocation().distanceSquared(original) > 9) {
            Lang.send(player, Lang.TRAPPED_MOVED_ABORTED);
            return;
        }

        ClaimManager claimManager = pl3xPlayer.getPlugin().getClaimManager();
        if (destination == null) {
            Location location = player.getLocation();
            while (destination == null) {
                Claim claim = claimManager.getClaim(location);
                if (claim != null) {
                    location = claim.getCoordinates()
                            .getMinLocation().clone()
                            .subtract(1, 0, 1);
                    continue;
                }

                Chunk chunk = location.getChunk();
                while (!chunk.isLoaded() || !chunk.load(true)) ;

                destination = location.getWorld()
                        .getHighestBlockAt(location)
                        .getLocation()
                        .add(0, 2, 0);
            }
        }

        player.teleport(destination);
        pl3xPlayer.setPendingRescue(false);

        logger.warn("Rescued trapped player " + player.getName());
        logger.warn("  from " + original);
        logger.warn("  to   " + destination);
    }
}
