package net.pl3x.bukkit.claims.visualization.task;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.event.VisualizeClaimsEvent;
import net.pl3x.bukkit.claims.visualization.Visualization;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VisualizationRevertTask extends BukkitRunnable {
    private final Pl3xClaims plugin;
    private final Player player;
    private final Visualization visualization;

    public VisualizationRevertTask(Pl3xClaims plugin, Player player, Visualization visualization) {
        this.plugin = plugin;
        this.player = player;
        this.visualization = visualization;
    }

    @Override
    public void run() {
        if (plugin.getPlayerManager().getPlayer(player).getVisualization() != visualization) {
            return; // visualizing something else already
        }
        VisualizeClaimsEvent event = new VisualizeClaimsEvent(player, null, null);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            visualization.revert(player);
        }
    }
}
