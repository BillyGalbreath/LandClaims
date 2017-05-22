package net.pl3x.bukkit.claims.claim;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.configuration.ClaimConfig;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class ClaimManager {
    private final Pl3xClaims plugin;

    public ClaimManager(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    private final Map<Long, Collection<Claim>> chunks = new HashMap<>();
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
        for (Claim claim : getTopLevelClaims()) {
            if (claim.getCoordinates().contains(location)) {
                for (Claim child : claim.getChildren()) {
                    if (child.getCoordinates().contains(location)) {
                        return child;
                    }
                }
                return claim;
            }
        }
        return null;
    }

    public Collection<Claim> getNearbyClaims(Location location) {
        Collection<Claim> claims = new HashSet<>();

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
        claim.getCoordinates().getChunkHashes(plugin).forEach(hash ->
                chunks.computeIfAbsent(hash, k -> new HashSet<>()).add(claim));
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

    public boolean deleteClaim(Claim claim) {
        return deleteClaim(claim, false);
    }

    public boolean deleteClaim(Claim claim, boolean deleteChildren) {
        if (claim == null) {
            return false; // nothing to delete
        }
        Collection<Claim> children = claim.getChildren();
        if (children.size() > 0) {
            if (!deleteChildren) {
                return false; // has child claims!
            }
            children.forEach(this::deleteClaim); // delete all child claims
        }
        chunks.forEach((k, v) -> v.removeIf(c -> c.getId() == claim.getId())); // remove claim's chunk hashes
        chunks.entrySet().removeIf(e -> e.getValue().isEmpty()); // remove empty chunk hashes from memory
        ClaimConfig.getConfig(plugin, claim.getId()).delete(); // delete the file
        topLevelClaims.remove(claim.getId()); // remove from memory
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

            // get the owner
            UUID owner = config.getOwner();
            if (owner == null) {
                plugin.getLog().error("   Could not get owner! Skipping.. (file " + id + ".yml)");
                continue;
            }

            // get the coordinates
            Coordinates coordinates = config.getCoordinates();
            if (coordinates == null) {
                plugin.getLog().error("   Could not get coordinates! Skipping.. (file " + id + ".yml)");
                // most likely the world is not loaded yet
                continue;
            }

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

            // everything looks good, make the claim
            Claim claim = new Claim(id, owner, parent, coordinates, isAdminClaim);

            // add to parent as child (if there is a parent)
            if (parent != null) {
                plugin.getLog().debug("   Claim " + id + " is a child of " + parentId);
                parent.addChild(claim);
            }

            // TODO flags
            //
            //

            // TODO trusts
            //
            //

            // TODO managers
            //
            //

            // finally store the claim in the manager
            addTopLevelClaim(claim);
            nextId = id + 1; // make sure next id is actually the next id...
            plugin.getLog().debug("   Claim " + id + " loaded successfully.");
            count++;
        }

        plugin.getLog().info("Finished loading all claims! (total: " + count + " / " + total + ")");
    }

    public void unloadClaims() {
        topLevelClaims.clear();
        ClaimConfig.removeAll();
    }

    public long getChunkHash(long x, long z) {
        return (z ^ (x << 32));
    }
}
