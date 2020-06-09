package net.pl3x.bukkit.claims.claim;

import net.pl3x.bukkit.claims.Logger;
import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.flag.FlagType;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Claim {
    public static final UUID PUBLIC_UUID = new UUID(0L, 0L);
    private final LandClaims plugin;
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
    private String entryMessage;
    private String exitMessage;

    public Claim(LandClaims plugin, long id, UUID owner, Claim parent, Coordinates coordinates, boolean isAdminClaim) {
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

    public String getOwnerName() {
        if (isAdminClaim || owner == null) {
            return "admin";
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(owner);
        return target == null ? "unknown" : (target.getName() == null ? "unknown" : target.getName());
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean isOwner(Player player) {
        if (owner == null && getParent() != null) {
            return getParent().isOwner(player);
        }
        return plugin.getPlayerManager().getPlayer(player).isIgnoringClaims() || (isAdminClaim && player.hasPermission("command.admin.adminclaims")) || isOwner(player.getUniqueId());
    }

    public boolean isOwner(UUID uuid) {
        return owner != null && owner.equals(uuid);
    }

    public int getLastActive() {
        if (isAdminClaim) {
            return 0;
        }
        OfflinePlayer owner = Bukkit.getOfflinePlayer(getOwner());
        if (owner.isOnline()) {
            return 0;
        }
        return (int) ((new Date().getTime() - new Date(owner.getLastPlayed()).getTime()) / 86400000);
    }

    public boolean hasEntryMessage() {
        return entryMessage != null && !entryMessage.isEmpty();
    }

    public String getEntryMessage() {
        return entryMessage;
    }

    public void setEntryMessage(String entryMessage) {
        this.entryMessage = entryMessage;
    }

    public boolean hasExitMessage() {
        return exitMessage != null && !exitMessage.isEmpty();
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
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
            trust = trusts.get(PUBLIC_UUID);
        }
        return trust;
    }

    public void setTrust(UUID uuid, TrustType trustType) {
        if (trustType == null) {
            plugin.getLog().debug("Removing trust for UUID: " + uuid);
            trusts.remove(uuid);
            return;
        }
        plugin.getLog().debug("Adding trust " + trustType.name() + " for UUID: " + uuid);
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
            if (player.hasPermission("command.admin.adminclaims")) {
                return true;
            }
        } else {
            if (player.hasPermission("command.admin.deleteclaim")) {
                return true;
            }
        }

        return parent != null ? parent.isOwner(player) : isOwner(player);
    }

    public boolean allowManage(Player player) {
        return isOwner(player) || managers.contains(player.getUniqueId()) ||
                (isAdminClaim() && player.hasPermission("command.admin.adminclaims"));
    }
}
