package net.pl3x.bukkit.claims.pl3xmap;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMapProvider;
import net.pl3x.map.api.SimpleLayerProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pl3xMapHook {
    private final LandClaims plugin;
    private final Map<UUID, Pl3xMapTask> provider = new HashMap<>();

    public Pl3xMapHook(LandClaims plugin) {
        this.plugin = plugin;

        Pl3xMapProvider.get().mapWorlds().forEach(world -> {
            if (Config.isWorldEnabled(world.name())) {
                SimpleLayerProvider provider = SimpleLayerProvider.builder("LandClaims").showControls(true).defaultHidden(false).build();
                world.layerRegistry().register(Key.of("landclaims_" + world.uuid()), provider);
                Pl3xMapTask task = new Pl3xMapTask(plugin, world, provider);
                task.runTaskTimerAsynchronously(plugin, 0, 20 * 300);
                this.provider.put(world.uuid(), task);
            }
        });
    }

    public void disable() {
        provider.values().forEach(Pl3xMapTask::disable);
        provider.clear();
    }
}
