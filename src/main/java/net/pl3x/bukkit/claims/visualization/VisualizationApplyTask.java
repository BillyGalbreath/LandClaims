package net.pl3x.bukkit.claims.visualization;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VisualizationApplyTask extends BukkitRunnable {
    private final Player player;
    private final Visualization visualization;

    public VisualizationApplyTask(Player player, Visualization visualization) {
        this.player = player;
        this.visualization = visualization;
    }

    @Override
    public void run() {
        for (VisualizationElement element : visualization.getElements()) {
            if (!element.getLocation().getChunk().isLoaded()) {
                continue;  // cheap distance check
            }
            //noinspection deprecation
            player.sendBlockChange(element.getLocation(), element.getMaterial(), element.getData());
        }

        Pl3xPlayer.getPlayer(player).setVisualization(visualization);

        //schedule automatic visualization reversion in 60 seconds.
        new VisualizationRevertTask(player, visualization).runTaskLater(Pl3xClaims.getPlugin(), 1200L);
    }
}
