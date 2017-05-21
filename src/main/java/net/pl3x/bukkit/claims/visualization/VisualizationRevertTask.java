package net.pl3x.bukkit.claims.visualization;

import net.pl3x.bukkit.claims.event.VisualizeClaimsEvent;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class VisualizationRevertTask extends BukkitRunnable {
    private final Player player;
    private final Visualization visualization;

    public VisualizationRevertTask(Player player, Visualization visualization) {
        this.player = player;
        this.visualization = visualization;
    }

    @Override
    public void run() {
        if (Pl3xPlayer.getPlayer(player).getVisualization() != visualization) {
            return; // visualizing something else already
        }

        Bukkit.getPluginManager().callEvent(new VisualizeClaimsEvent(player, Collections.emptySet()));

        Visualization.revert(player);
    }
}
