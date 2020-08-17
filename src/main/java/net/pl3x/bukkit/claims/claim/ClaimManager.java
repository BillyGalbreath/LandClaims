package net.pl3x.bukkit.claims.claim;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.Logger;
import net.pl3x.bukkit.claims.configuration.ClaimConfig;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.event.claim.ResizeClaimEvent;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class ClaimManager {
    private final LandClaims plugin;

    public ClaimManager(LandClaims plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Map<Long, Collection<Claim>>> chunks = new HashMap<>();
    private final Map<Long, Claim> topLevelClaims = new HashMap<>();
    private long nextId = 0;

    public long getNextId() {
        return nextId++;
    }

    public Collection<Claim> getTopLevelClaims() {
        return topLevelClaims.values();
    }

    public Claim getClaim(long id) {
        return topLevelClaims.get(id);
    }

    public Claim getClaim(Location location) {
        return getTopLevelClaims().stream()
                .filter(topLevelClaim -> topLevelClaim.getCoordinates().contains(location))
                .findFirst().map(topLevelClaim -> topLevelClaim.getChildren().stream()
                        .filter(child -> child.getCoordinates().contains(location))
                        .findFirst().orElse(topLevelClaim)).orElse(null);
    }

    public Collection<Claim> getNearbyClaims(Location location) {
        Collection<Claim> claims = new HashSet<>();
        Map<Long, Collection<Claim>> chunks = getChunks(location.getWorld());

        Chunk minChunk = location.getWorld().getChunkAt(location.subtract(150, 0, 150));
        Chunk maxChunk = location.getWorld().getChunkAt(location.add(300, 0, 300));

        for (int x = minChunk.getX(); x <= maxChunk.getX(); x++) {
            for (int z = minChunk.getZ(); z <= maxChunk.getZ(); z++) {
                Collection<Claim> claimsInChunk = chunks.get(getChunkHash(x, z));
                if (claimsInChunk != null) {
                    claims.addAll(claimsInChunk);
                }
            }
        }
        return claims;
    }

    public void addTopLevelClaim(Claim claim) {
        topLevelClaims.put(claim.getId(), claim);
        calculateChunkHashes(claim);
    }

    public void createNewClaim(Claim claim) {
        if (claim.getParent() == null) {
            addTopLevelClaim(claim);
        } else {
            claim.getParent().addChild(claim);
        }
        ClaimConfig config = ClaimConfig.getConfig(plugin, claim.getId());
        config.setId(claim.getId());
        config.setAdminClaim(claim.isAdminClaim());
        config.setParent(claim.getParent());
        config.setOwner(claim.getOwner());
        config.setCoordinates(claim.getCoordinates());
        config.save();
    }

    public void resizeClaim(Player player, Claim claim, Coordinates newCoords) {
        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player);
        Coordinates oldCoords = claim.getCoordinates();
        plugin.getLog().debug(player.getName() + " is Resizing Claim #" + claim.getId());
        plugin.getLog().debug("    Old Coords: " + oldCoords);
        plugin.getLog().debug("    New Coords: " + newCoords);

        // check top level claim size rules and permissions
        // admin claims bypass this check
        if (claim.getParent() == null && !claim.isAdminClaim()) {
            // re-check minimum size requirements if shrinking
            if (newCoords.getWidthX() < oldCoords.getWidthX() || newCoords.getWidthZ() < oldCoords.getWidthZ()) {
                if (newCoords.getWidthX() < Config.CLAIMS_MIN_WIDTH || newCoords.getWidthZ() < Config.CLAIMS_MIN_WIDTH) {
                    Lang.send(player, Lang.RESIZE_FAILED_TOO_NARROW
                            .replace("{minimum}", Integer.toString(Config.CLAIMS_MIN_WIDTH)));
                    return;
                }
                if (newCoords.getArea() < Config.CLAIMS_MIN_AREA) {
                    Lang.send(player, Lang.RESIZE_FAILED_TOO_SMALL
                            .replace("{minimum}", Integer.toString(Config.CLAIMS_MIN_AREA)));
                    return;
                }
            }

            // check if player has enough claim blocks
            if (claim.isOwner(player)) {
                int remaining = pl3xPlayer.getRemainingClaimBlocks() + oldCoords.getArea() - newCoords.getArea();
                if (remaining < 0) {
                    Lang.send(player, Lang.RESIZE_FAILED_NEED_MORE_BLOCKS
                            .replace("{amount}", Integer.toString(-remaining)));
                    return;
                }
            }
        }

        // check for overlaps
        if (claim.getParent() == null) {
            // check for overlapping other top claims
            for (Claim topLevelClaim : plugin.getClaimManager().getTopLevelClaims()) {
                if (topLevelClaim == claim) {
                    continue;
                }
                if (topLevelClaim.getCoordinates().overlaps(newCoords)) {
                    Lang.send(player, Lang.RESIZE_FAILED_OVERLAP);
                    pl3xPlayer.showVisualization(topLevelClaim, VisualizationType.ERROR);
                    return;
                }
            }
        } else {
            // check if overlapping parent boundary
            if (!claim.getParent().getCoordinates().contains(newCoords)) {
                Lang.send(player, Lang.RESIZE_FAILED_CHILD_OVERLAP_PARENT);
                return;
            }

            // check for overlapping other children
            for (Claim child : claim.getParent().getChildren()) {
                if (child.getId() == claim.getId()) {
                    continue;
                }
                if (child.getCoordinates().overlaps(newCoords)) {
                    Lang.send(player, Lang.RESIZE_FAILED_CHILD_OVERLAP);
                    return;
                }
            }
        }

        ResizeClaimEvent resizeClaimEvent = new ResizeClaimEvent(player, claim, newCoords);
        Bukkit.getPluginManager().callEvent(resizeClaimEvent);
        if (resizeClaimEvent.isCancelled()) {
            return; // cancelled by plugin
        }

        // resize the claim
        oldCoords.resize(newCoords);
        calculateChunkHashes(claim);
        ClaimConfig claimConfig = ClaimConfig.getConfig(plugin, claim.getId());
        claimConfig.setCoordinates(oldCoords);
        claimConfig.save();

        int remaining = pl3xPlayer.getRemainingClaimBlocks();
        UUID owner = claim.getParent() != null ? claim.getParent().getOwner() : claim.getOwner();
        if (!player.getUniqueId().equals(owner)) {
            if (claim.isAdminClaim()) {
                remaining = 0;
            } else {
                remaining = plugin.getPlayerManager().getPlayer(owner).getRemainingClaimBlocks();
                if (!Bukkit.getOfflinePlayer(owner).isOnline()) {
                    plugin.getPlayerManager().unload(owner);
                }
            }
        }

        Lang.send(player, Lang.RESIZE_SUCCESS
                .replace("{amount}", Integer.toString(remaining)));
        pl3xPlayer.showVisualization(claim);

        pl3xPlayer.setLastToolLocation(null);
        pl3xPlayer.setResizingClaim(null);
        pl3xPlayer.setParentClaim(null);
    }

    public boolean deleteClaim(Claim claim) {
        return deleteClaim(claim, false);
    }

    public boolean deleteClaim(Claim claim, boolean deleteChildren) {
        if (claim == null) {
            return false; // nothing to delete
        }

        if (claim.getParent() != null) {
            claim.getParent().removeChild(claim);
        } else {
            // check for children
            Collection<Claim> children = new HashSet<>(claim.getChildren());
            if (children.size() > 0) {
                if (!deleteChildren) {
                    return false; // has child claims!
                }
                children.forEach(this::deleteClaim); // delete all child claims
            }
            removeChunkHashes(claim);
            topLevelClaims.remove(claim.getId()); // remove from memory
        }
        ClaimConfig.getConfig(plugin, claim.getId()).delete(); // delete the file
        return true;
    }

    public void loadClaims() {
        unloadClaims();

        // load up all the config files first
        File[] citiesList = new File(plugin.getDataFolder(), ClaimConfig.CLAIM_DIRECTORY)
                .listFiles((dir, name) -> name.endsWith(".yml"));
        if (citiesList == null) {
            citiesList = new File[0];
        }
        for (File file : citiesList) {
            ClaimConfig.getConfig(plugin, Long.parseLong(file.getName().split(".yml")[0]));
        }

        // iterate all configs and create claims starting from lowest id number
        TreeMap<Long, ClaimConfig> treeMap = new TreeMap<>(ClaimConfig.getConfigs());
        long count = 0; // lets count how many claims we actually load up
        long total = treeMap.size();
        plugin.getLog().info("Loading all claims (total: " + total + ")");
        for (Map.Entry<Long, ClaimConfig> entry : treeMap.entrySet()) {
            long id = entry.getKey();
            ClaimConfig config = entry.getValue();

            // sanity check the id
            if (config.getId() < 0 || config.getId() != id) {
                plugin.getLog().error("   Claim id mismatch! Skipping.. (file " + id + ".yml)");
                continue;
            }

            // get if an admin claim or not
            boolean isAdminClaim = config.isAdminClaim();

            // get the parent (if any)
            long parentId = config.getParentId();
            Claim parent = null;
            if (parentId > -1) {
                // there is a parent, lets make sure its already been stored in manager
                parent = getClaim(parentId);
                if (parent == null) {
                    plugin.getLog().error("   Could not get parent! Skipping.. (file " + id + ".yml)");
                    continue;
                }
            }

            // get the owner
            UUID owner = config.getOwner();
            if (owner == null && !isAdminClaim) {
                if (parent != null) {
                    owner = parent.getOwner();
                    config.setOwner(owner);
                    config.save();
                } else {
                    plugin.getLog().error("   Could not get owner! Skipping.. (file " + id + ".yml)");
                    continue;
                }
            }

            // get the coordinates
            Coordinates coordinates = config.getCoordinates();
            if (coordinates == null) {
                plugin.getLog().error("   Could not get coordinates! Skipping.. (file " + id + ".yml)");
                // most likely the world is not loaded yet
                continue;
            }

            // everything looks good, make the claim
            Claim claim = new Claim(plugin, id, owner, parent, coordinates, isAdminClaim);

            // Entry and exit messages
            String entryMessage = config.getEntryMessage();
            if (entryMessage != null && !entryMessage.isEmpty()) {
                claim.setEntryMessage(entryMessage);
            }

            String exitMessage = config.getExitMessage();
            if (exitMessage != null && !exitMessage.isEmpty()) {
                claim.setExitMessage(exitMessage);
            }

            // trusts
            claim.getTrusts().putAll(config.getTrusts());
            claim.getManagers().addAll(config.getManagers());

            // flags
            config.getFlags().forEach(claim::setFlag);

            Logger logger = plugin.getLog();
            logger.debug("  id: " + id);
            logger.debug("  owner: " + owner);
            logger.debug("  parent: " + (parent == null ? "null" : parent.getId()));
            logger.debug("  coords: " + coordinates.toString());
            logger.debug("  isAdminClaim: " + isAdminClaim);
            logger.debug("  trusts: " + claim.getTrusts());
            logger.debug("  flags: " + claim.getFlags());

            // finally store the claim
            if (parent != null) {
                // add to parent as child (if there is a parent)
                parent.addChild(claim);
            } else {
                // add to chunk map
                addTopLevelClaim(claim);
            }

            nextId = id + 1; // make sure next id is actually the next id...
            plugin.getLog().debug("   Claim #" + id + " loaded successfully.");
            count++;
        }

        plugin.getLog().info("Finished loading all claims! (total: " + count + " / " + total + ")");
    }

    public void unloadClaims() {
        topLevelClaims.clear();
        ClaimConfig.removeAll();
    }

    public void removeChunkHashes(Claim claim) {
        Map<Long, Collection<Claim>> chunks = getChunks(claim.getWorld());
        chunks.forEach((k, v) -> v.removeIf(c -> c.getId() == claim.getId())); // remove claim's chunk hashes
        chunks.entrySet().removeIf(e -> e.getValue().isEmpty()); // remove empty chunk hashes from memory
        putChunks(claim.getWorld(), chunks);
    }

    public Map<Long, Collection<Claim>> getChunks(World world) {
        Map<Long, Collection<Claim>> chunks = this.chunks.get(world.getUID());
        if (chunks == null) {
            chunks = new HashMap<>();
        }
        return chunks;
    }

    public void putChunks(World world, Map<Long, Collection<Claim>> chunks) {
        this.chunks.put(world.getUID(), chunks);
    }

    public void calculateChunkHashes(Claim claim) {
        removeChunkHashes(claim);
        Map<Long, Collection<Claim>> chunks = getChunks(claim.getWorld());
        claim.getCoordinates().getChunkHashes(plugin).forEach(hash ->
                chunks.computeIfAbsent(hash, k -> new HashSet<>()).add(claim));
        putChunks(claim.getWorld(), chunks);
    }

    public long getChunkHash(long x, long z) {
        return (z ^ (x << 32));
    }
}
