package net.pl3x.bukkit.claims.configuration;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.Coordinates;
import net.pl3x.bukkit.claims.claim.TrustType;
import net.pl3x.bukkit.claims.claim.flag.FlagType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClaimConfig extends YamlConfiguration {
    public static final String CLAIM_DIRECTORY = "claimdata";
    private static final Map<Long, ClaimConfig> configs = new HashMap<>();

    public static Map<Long, ClaimConfig> getConfigs() {
        synchronized (configs) {
            return configs;
        }
    }

    public static ClaimConfig getConfig(LandClaims plugin, long id) {
        synchronized (configs) {
            return configs.computeIfAbsent(id, k -> new ClaimConfig(plugin, id));
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

    private final LandClaims plugin;
    private final long claimId;
    private final File file;
    private final Object saveLock = new Object();

    private ClaimConfig(LandClaims plugin, long claimId) {
        super();
        this.plugin = plugin;
        this.claimId = claimId;
        this.file = new File(plugin.getDataFolder(),
                CLAIM_DIRECTORY + File.separator + claimId + ".yml");
        if (!file.exists()) {
            save();
        }
        reload();
    }

    public void reload() {
        synchronized (saveLock) {
            try {
                load(file);
            } catch (Exception e) {
                plugin.getLog().error("Could not load claim config file! (" + claimId + ".yml)");
                plugin.getLog().error("Details of why:");
                e.printStackTrace();
            }
        }
    }

    public void save() {
        synchronized (saveLock) {
            try {
                save(file);
            } catch (Exception e) {
                plugin.getLog().error("Could not save claim config file! (" + claimId + ".yml)");
                plugin.getLog().error("Details of why:");
                e.printStackTrace();
            }
        }
    }

    public void delete() {
        synchronized (saveLock) {
            if (!file.delete()) {
                plugin.getLog().error("Could not delete claim file! (" + claimId + ".yml)");
            }
        }
    }

    public long getClaimId() {
        return claimId;
    }

    public void setId(long id) {
        set("id", id);
    }

    public void setAdminClaim(boolean isAdminClaim) {
        set("admin", isAdminClaim);
    }

    public void setOwner(UUID owner) {
        set("owner", owner == null ? null : owner.toString());
    }

    public void setParent(Claim parent) {
        set("parent", parent == null ? -1 : parent.getId());
    }

    public void setCoordinates(Coordinates coordinates) {
        set("coordinates.world", coordinates.getWorld().getName());
        set("coordinates.min.x", coordinates.getMinX());
        set("coordinates.min.z", coordinates.getMinZ());
        set("coordinates.max.x", coordinates.getMaxX());
        set("coordinates.max.z", coordinates.getMaxZ());
    }

    public void setEntryMessage(String entryMessage) {
        set("entry-message", entryMessage);
    }

    public void setExitMessage(String exitMessage) {
        set("exit-message", exitMessage);
    }

    public void setTrusts(Map<UUID, TrustType> trusts) {
        set("trusts.builders", trusts.entrySet().stream()
                .filter(entry -> entry.getValue() == TrustType.BUILDER)
                .map(entry -> uuidToString(entry.getKey()))
                .collect(Collectors.toList()));
        set("trusts.containers", trusts.entrySet().stream()
                .filter(entry -> entry.getValue() == TrustType.CONTAINER)
                .map(entry -> uuidToString(entry.getKey()))
                .collect(Collectors.toList()));
        set("trusts.accessors", trusts.entrySet().stream()
                .filter(entry -> entry.getValue() == TrustType.ACCESS)
                .map(entry -> uuidToString(entry.getKey()))
                .collect(Collectors.toList()));
    }

    public void setManagers(Collection<UUID> managers) {
        set("trusts.managers", managers.stream()
                .map(uuid -> uuid == null ? "null" : uuid.toString())
                .collect(Collectors.toList()));
    }

    public void setFlags(Map<FlagType, Boolean> flags) {
        set("flags", null);
        flags.forEach((flag, value) -> set("flags." + flag.name().toLowerCase(), value));
    }

    public long getId() {
        return (long) getInt("id", -1);
    }

    public boolean isAdminClaim() {
        return getBoolean("admin", false);
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

    public String getEntryMessage() {
        return getString("entry-message", null);
    }

    public String getExitMessage() {
        return getString("exit-message", null);
    }

    public Map<UUID, TrustType> getTrusts() {
        Map<UUID, TrustType> trusts = new HashMap<>();
        getStringList("trusts.builders").stream()
                .map(this::stringToUUID)
                .forEach(uuid -> trusts.put(uuid, TrustType.BUILDER));
        getStringList("trusts.containers").stream()
                .map(this::stringToUUID)
                .forEach(uuid -> trusts.put(uuid, TrustType.CONTAINER));
        getStringList("trusts.accessors").stream()
                .map(this::stringToUUID)
                .forEach(uuid -> trusts.put(uuid, TrustType.ACCESS));
        return trusts;
    }

    public Collection<UUID> getManagers() {
        List<String> managers = getStringList("trusts.managers");
        return managers == null ? new HashSet<>() : managers.stream()
                .map(this::stringToUUID)
                .collect(Collectors.toSet());
    }

    public Map<FlagType, Boolean> getFlags() {
        Map<FlagType, Boolean> flags = new HashMap<>();
        ConfigurationSection section = getConfigurationSection("flags");
        if (section == null) {
            return flags;
        }
        Set<String> keys = section.getKeys(false);
        if (keys == null) {
            return flags;
        }
        keys.stream()
                .filter(flag -> FlagType.getType(flag) != null)
                .forEach(flag -> flags.put(FlagType.getType(flag), section.getBoolean(flag)));
        return flags;
    }

    private String uuidToString(UUID uuid) {
        return (uuid == null ? Claim.PUBLIC_UUID : uuid).toString();
    }

    private UUID stringToUUID(String s) {
        return s == null || s.isEmpty() ? Claim.PUBLIC_UUID : UUID.fromString(s);
    }
}
