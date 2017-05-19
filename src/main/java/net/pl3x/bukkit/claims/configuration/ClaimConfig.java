package net.pl3x.bukkit.claims.configuration;

import net.pl3x.bukkit.claims.Logger;
import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Coordinates;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimConfig extends YamlConfiguration {
    public static final String CLAIM_DIRECTORY = "claimdata";
    private static final Map<Long, ClaimConfig> configs = new HashMap<>();

    public static Map<Long, ClaimConfig> getConfigs() {
        return configs;
    }

    public static ClaimConfig getConfig(long id) {
        synchronized (configs) {
            return configs.computeIfAbsent(id, k -> new ClaimConfig(id));
        }
    }

    public static void removeConfig(long id) {
        synchronized (configs) {
            configs.remove(id);
        }
    }

    public static void removeAll() {
        synchronized (configs) {
            configs.clear();
        }
    }

    private final long claimId;
    private final File file;
    private final Object saveLock = new Object();

    private ClaimConfig(long claimId) {
        super();
        this.claimId = claimId;
        this.file = new File(Pl3xClaims.getPlugin().getDataFolder(),
                CLAIM_DIRECTORY + File.separator + claimId + ".yml");
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

    public void delete() {
        synchronized (saveLock) {
            if (!file.delete()) {
                Logger.error("Could not delete claim file: " + claimId);
            }
        }
    }

    public long getId() {
        return getLong("id", -1);
    }

    public UUID getOwner() {
        String owner = getString("owner");
        return owner == null || owner.isEmpty() ? null : UUID.fromString(owner);
    }

    public long getParentId() {
        return getLong("parent", -1);
    }

    public Coordinates getCoordinates() {
        try {
            return new Coordinates(
                    Bukkit.getWorld(getString("coordinates.world", "")),
                    getInt("coordinates.min.x"),
                    getInt("coordinates.min.z"),
                    getInt("coordinates.max.x"),
                    getInt("coordinates.max.z"));
        } catch (Exception e) {
            return null;
        }
    }
}
