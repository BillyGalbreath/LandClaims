package net.pl3x.bukkit.claims.configuration;

import net.pl3x.bukkit.claims.Pl3xClaims;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public abstract class PlayerConfig extends YamlConfiguration {
    private final File file;
    private final Object saveLock = new Object();

    public PlayerConfig(Pl3xClaims plugin, UUID uuid) {
        super();
        this.file = new File(plugin.getDataFolder(),
                "userdata" + File.separator + uuid.toString() + ".yml");
        if (!file.exists()) {
            save(); // api will make sure parent directories are made
        }
        reload();
    }

    public void reload() {
        synchronized (saveLock) {
            try {
                load(file);
            } catch (Exception ignore) {
            }
        }
    }

    protected void save() {
        synchronized (saveLock) {
            try {
                save(file);
            } catch (Exception ignore) {
            }
        }
    }
}
