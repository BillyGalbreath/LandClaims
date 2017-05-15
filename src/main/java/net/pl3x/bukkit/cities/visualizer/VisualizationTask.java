package net.pl3x.bukkit.cities.visualizer;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VisualizationTask extends BukkitRunnable {
    private final Map<Visualization, Set<Player>> visualizations = new ConcurrentHashMap<>();

    @Override
    public void run() {
        for (Map.Entry<Visualization, Set<Player>> entry : visualizations.entrySet()) {
            // show visualization to all watchers
            for (Player watcher : entry.getValue()) {
                entry.getKey().sendPackets(watcher);
            }
        }
    }

    public void addWatcher(Visualization visualization, Player watcher) {
        Set<Player> watchers = visualizations.get(visualization);
        if (watchers == null) {
            watchers = new HashSet<>();
        }
        watchers.add(watcher);
        visualizations.put(visualization, watchers);
    }

    public void removeWatcher(Visualization visualization, Player watcher) {
        Set<Player> watchers = visualizations.get(visualization);
        if (watchers == null) {
            return; // nothing to remove
        }
        watchers.remove(watcher);
        if (watchers.isEmpty()) {
            removeVisualization(visualization);
            return; // removed unwatched visualization
        }
        visualizations.put(visualization, watchers);
    }

    public void removeVisualization(Visualization visualization) {
        visualizations.remove(visualization);
    }
}
