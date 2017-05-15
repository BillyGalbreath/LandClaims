package net.pl3x.bukkit.cities.configuration;

import net.pl3x.bukkit.cities.Pl3xCities;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public abstract class PlayerConfig extends YamlConfiguration {
    private final File file;
    private final UUID uuid;
    private final Object saveLock = new Object();

    public PlayerConfig(UUID uuid) {
        super();
        this.uuid = uuid;
        this.file = new File(Pl3xCities.getPlugin().getDataFolder(),
                "userdata" + File.separator + uuid.toString() + ".yml");
        if (!file.exists()) {
            save();
        }
        reload();
    }

    protected void reload() {
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
