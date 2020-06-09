package net.pl3x.bukkit.claims.visualization.task;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.visualization.Visualization;
import net.pl3x.bukkit.claims.visualization.VisualizationElement;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VisualizationApplyTask extends BukkitRunnable {
    private final LandClaims plugin;
    private final Player player;
    private final Visualization visualization;

    public VisualizationApplyTask(LandClaims plugin, Player player, Visualization visualization) {
        this.plugin = plugin;
        this.player = player;
        this.visualization = visualization;
    }

    @Override
    public void run() {
        for (VisualizationElement element : visualization.getElements()) {
            if (!element.getLocation().getChunk().isLoaded()) {
                continue;  // cheap distance check
            }
            player.sendBlockChange(element.getLocation(), element.getBlockData());
        }

        plugin.getPlayerManager().getPlayer(player).setVisualization(visualization);

        //schedule automatic visualization reversion in 60 seconds.
        new VisualizationRevertTask(plugin, player, visualization).runTaskLater(plugin, 1200L);
    }
}
