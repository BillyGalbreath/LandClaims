package net.pl3x.bukkit.claims.dynmap;

import org.bukkit.scheduler.BukkitRunnable;

public class DynmapUpdate extends BukkitRunnable {
    private final DynmapHook dynmapHook;

    DynmapUpdate(DynmapHook dynmapHook) {
        this.dynmapHook = dynmapHook;
    }

    public void run() {
        if (!dynmapHook.stop) {
            dynmapHook.updateClaims();
        }
    }
}
