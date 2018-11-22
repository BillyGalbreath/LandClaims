package net.pl3x.bukkit.claims.visualization.task;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.visualization.Visualization;
import net.pl3x.bukkit.claims.visualization.VisualizationElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VisualizationApplyTask extends BukkitRunnable {
    private final Pl3xClaims plugin;
    private final Player player;
    private final Visualization visualization;

    public VisualizationApplyTask(Pl3xClaims plugin, Player player, Visualization visualization) {
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
            //noinspection deprecation
            player.sendBlockChange(element.getLocation(), element.getMaterial(), element.getMaterial() == Material.REDSTONE_ORE ? 1 : element.getData());
        }

        plugin.getPlayerManager().getPlayer(player).setVisualization(visualization);

        //schedule automatic visualization reversion in 60 seconds.
        new VisualizationRevertTask(plugin, player, visualization).runTaskLater(plugin, 1200L);
    }
}
