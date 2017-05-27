package net.pl3x.bukkit.claims.player.task;

import net.pl3x.bukkit.claims.Logger;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.event.player.AccrueClaimBlocksEvent;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AccrueClaimBlocksTask extends BukkitRunnable {
    private final Pl3xPlayer pl3xPlayer;
    private final int idleThreshold;
    private final Logger logger;

    public AccrueClaimBlocksTask(Pl3xPlayer pl3xPlayer) {
        this.pl3xPlayer = pl3xPlayer;
        this.idleThreshold = Config.IDLE_THRESHOLD * Config.IDLE_THRESHOLD;
        this.logger = pl3xPlayer.getPlugin().getLog();
    }

    @Override
    public void run() {
        if (pl3xPlayer.getPlayer() == null || !pl3xPlayer.getPlayer().isOnline()) {
            return;
        }

        Player player = pl3xPlayer.getPlayer().getPlayer();

        boolean idle = false;
        try {
            idle = player.isInsideVehicle() ||
                    player.getLocation().getBlock().isLiquid() ||
                    !(pl3xPlayer.getLastIdleCheckLocation() == null ||
                            pl3xPlayer.getLastIdleCheckLocation().distanceSquared(player.getLocation()) > idleThreshold);
        } catch (IllegalArgumentException ignore) {
            // can't measure distance between different worlds
        }

        pl3xPlayer.updateLastIdleCheckLocation();

        int accrualRate = Config.ACCRUED_PER_HOUR;

        if (idle) {
            if (Config.ACCRUED_IDLE_PERCENT <= 0) {
                logger.debug(player.getName() + " wasn't active enough to accrue claim blocks this round");
                return; // idle accrual percentage is disabled
            }

            accrualRate = (int) (accrualRate * (Config.ACCRUED_IDLE_PERCENT / 100.0D));
        }

        AccrueClaimBlocksEvent accrueClaimBlocksEvent = new AccrueClaimBlocksEvent(player, accrualRate, idle);
        Bukkit.getPluginManager().callEvent(accrueClaimBlocksEvent);
        if (accrueClaimBlocksEvent.isCancelled()) {
            logger.debug(player.getName() + " claim block delivery was canceled by another plugin");
            return; // cancelled by another plugin
        }

        int accruedAmount = accrueClaimBlocksEvent.getAccrualRate() / 6; // earned every 10 minutes (6 times per hour)
        if (accruedAmount < 0) {
            logger.debug(player.getName() + " claim block delivery was negative. Skipping");
            return;
        }

        pl3xPlayer.setClaimBlocks(pl3xPlayer.getClaimBlocks() + accruedAmount);
        logger.debug("Added " + accruedAmount + " claim blocks to " + player.getName() + " (" + accrualRate + " per hour)");
    }
}
