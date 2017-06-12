package net.pl3x.bukkit.claims.claim;

import net.pl3x.bukkit.claims.Logger;
import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.flag.FlagType;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Claim {
    private final Pl3xClaims plugin;
    private final Logger logger;
    private final long id;
    private UUID owner;
    private final Claim parent;
    private final Coordinates coordinates;
    private boolean isAdminClaim;
    private final Map<FlagType, Boolean> flags = new HashMap<>();
    private final Map<UUID, TrustType> trusts = new HashMap<>();
    private final Collection<UUID> managers = new HashSet<>();
    private final Collection<Claim> children = new HashSet<>();

    public Claim(Pl3xClaims plugin, long id, UUID owner, Claim parent, Coordinates coordinates, boolean isAdminClaim) {
        this.plugin = plugin;
        this.logger = plugin.getLog();
        this.id = id;
        this.owner = owner;
        this.parent = parent;
        this.coordinates = coordinates;
        this.isAdminClaim = isAdminClaim;
    }

    public long getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean isOwner(Player player) {
        return isAdminClaim && player.hasPermission("command.adminclaims") || isOwner(player.getUniqueId());
    }

    public boolean isOwner(UUID uuid) {
        return owner != null && owner.equals(uuid);
    }

    public Claim getParent() {
        return parent;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public boolean isAdminClaim() {
        return isAdminClaim;
    }

    public void setAdminClaim(boolean isAdminClaim) {
        this.isAdminClaim = isAdminClaim;
    }

    public Map<FlagType, Boolean> getFlags() {
        return flags;
    }

    public Boolean getFlag(FlagType flag) {
        Boolean value = flags.get(flag);
        if (value == null) {
            value = parent == null ? flag.getDefault() : parent.getFlag(flag);
        }
        return value;
    }

    public void setFlag(FlagType flag, Boolean value) {
        if (value == null) {
            flags.remove(flag);
            return;
        }
        flags.put(flag, value);
    }

    public String getFlagsList() {
        return String.join("", flags.entrySet().stream()
                .map(entry -> Lang.INSPECT_BLOCK_FLAGS
                        .replace("{flag}", entry.getKey().name())
                        .replace("{value}", entry.getValue() ? "allow" : "deny"))
                .collect(Collectors.toList()));
    }

    public Map<UUID, TrustType> getTrusts() {
        return trusts;
    }

    public TrustType getTrust(Player player) {
        return getTrust(player.getUniqueId());
    }

    public void setTrust(Player player, TrustType trustType) {
        setTrust(player.getUniqueId(), trustType);
    }

    public TrustType getTrust(UUID uuid) {
        TrustType trust = trusts.get(uuid);
        if (trust == null) {
            trust = trusts.get(null);
        }
        return trust;
    }

    public void setTrust(UUID uuid, TrustType trustType) {
        if (trustType == null) {
            trusts.remove(uuid);
            return;
        }
        trusts.put(uuid, trustType);
    }

    public Collection<UUID> getManagers() {
        return managers;
    }

    public void addManager(UUID uuid) {
        managers.add(uuid);
    }

    public void removeManager(UUID uuid) {
        managers.remove(uuid);
    }

    public Collection<Claim> getChildren() {
        return children;
    }

    public void addChild(Claim claim) {
        children.add(claim);
    }

    public void removeChild(Claim claim) {
        children.remove(claim);
    }

    public boolean allowAccess(Player player) {
        if (isOwner(player)) {
            return true;
        }
        TrustType trust = getTrust(player);
        if (trust == null) {
            logger.debug("allowAccess: trusts empty (" + id + ")");
            return false;
        }
        switch (trust) {
            case ACCESS:
            case CONTAINER:
            case BUILDER:
                return true;
            default:
                logger.debug("allowAccess: not trusted (" + player.getUniqueId() + " " + id + ")");
                return false;
        }
    }

    public boolean allowContainers(Player player) {
        if (isOwner(player)) {
            return true;
        }
        TrustType trust = getTrust(player);
        if (trust == null) {
            logger.debug("allowContainers: trusts empty (" + id + ")");
            return false;
        }
        switch (trust) {
            case CONTAINER:
            case BUILDER:
                return true;
            default:
                logger.debug("allowContainers: not trusted (" + player.getUniqueId() + " " + id + ")");
                return false;
        }
    }

    public boolean allowBuild(Player player) {
        if (isOwner(player)) {
            return true;
        }
        TrustType trust = getTrust(player);
        if (trust == null) {
            logger.debug("allowBuild: trusts empty (" + id + ")");
            return false;
        }
        switch (trust) {
            case BUILDER:
                return true;
            default:
                logger.debug("allowBuild: not trusted (" + player.getUniqueId() + " " + id + ")");
                return false;
        }
    }

    public boolean allowEdit(Player player) {
        if (player == null) {
            logger.debug("allowEdit: null player (" + id + ")");
            return false;
        }

        if (isAdminClaim) {
            if (player.hasPermission("command.adminclaims")) {
                return true;
            }
        } else {
            if (player.hasPermission("command.deleteclaim")) {
                return true;
            }
        }

        return parent != null ? parent.isOwner(player) : isOwner(player);
    }

    public boolean allowManage(Player player) {
        return isOwner(player) || managers.contains(player.getUniqueId()) ||
                (isAdminClaim() && player.hasPermission("command.adminclaims"));
    }
}
