package net.pl3x.bukkit.claims.claim;

import net.pl3x.bukkit.claims.Logger;
import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.configuration.ClaimConfig;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class ClaimManager {
    private static final ClaimManager instance = new ClaimManager();

    public static ClaimManager getInstance() {
        return instance;
    }

    private ClaimManager() {
    }

    private final Map<Long, Claim> claims = new HashMap<>();
    private long nextId = 0;

    public long getNextId() {
        return nextId++;
    }

    public Collection<Claim> getClaims() {
        return claims.values();
    }

    public Claim getClaim(long id) {
        return claims.get(id);
    }

    public Claim getClaim(Location location) {
        for (Claim claim : getClaims()) {
            if (claim.getCoordinates().contains(location)) {
                return claim;
            }
        }
        return null;
    }

    public void addClaim(Claim claim) {
        claims.put(claim.getId(), claim);
    }

    public boolean deleteClaim(Claim claim) {
        return deleteClaim(claim.getId(), false);
    }

    public boolean deleteClaim(long id) {
        return deleteClaim(id, false);
    }

    public boolean deleteClaim(Claim claim, boolean deleteChildren) {
        return deleteClaim(claim.getId(), deleteChildren);
    }

    public boolean deleteClaim(long id, boolean deleteChildren) {
        Claim claim = claims.get(id);
        if (claim == null) {
            return false; // nothing to delete
        }
        Collection<Claim> children = claim.getChildren();
        if (children.size() > 0) {
            if (!deleteChildren) {
                return false; // has child claims!
            }
            children.forEach(this::deleteClaim); // delete all children claims
        }
        ClaimConfig.getConfig(claim.getId()).delete(); // delete the file
        claims.remove(id); // remove from memory
        return true;
    }

    public void loadClaims() {
        unloadClaims();

        // load up all the config files first
        try {
            Files.list(new File(Pl3xClaims.getPlugin().getDataFolder(), ClaimConfig.CLAIM_DIRECTORY).toPath())
                    .filter(path -> path.getFileName().endsWith(".yml"))
                    .forEach(path -> ClaimConfig.getConfig(Long.parseLong(path.getFileName().toString().split(".yml")[0])));
        } catch (IOException ignore) {
        }

        // iterate all config files and create claims starting from lowest id number
        TreeMap<Long, ClaimConfig> treeMap = new TreeMap<>(ClaimConfig.getConfigs());
        long count = 0; // lets count how many claims we actually load up
        long total = treeMap.size();
        Logger.info("Loading all claims (total: " + total + ")");
        for (Map.Entry<Long, ClaimConfig> entry : treeMap.entrySet()) {
            long id = entry.getKey();
            ClaimConfig config = entry.getValue();

            // sanity check the id
            Logger.debug("Loading claim " + id);
            if (config.getId() < 0 || config.getId() != id) {
                Logger.error("   Claim id mismatch! Skipping.. (file " + id + ".yml)");
                continue;
            }

            // get the owner
            UUID owner = config.getOwner();
            if (owner == null) {
                Logger.error("   Could not get owner! Skipping.. (file " + id + ".yml)");
                continue;
            }

            // get the coordinates
            Coordinates coordinates = config.getCoordinates();
            if (coordinates == null) {
                Logger.error("   Could not get coordinates! Skipping.. (file " + id + ".yml)");
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
                    Logger.error("   Could not get parent! Skipping.. (file " + id + ".yml)");
                    continue;
                }
            }

            // everything looks good, make the claim
            Claim claim = new Claim(id, owner, parent, coordinates);

            // add to parent as child (if there is a parent)
            if (parent != null) {
                Logger.debug("   Claim " + id + " is a child of " + parentId);
                parent.addChild(claim);
            }

            // TODO flags

            // TODO trusts

            // TODO managers

            // finally store the claim in the manager
            claims.put(id, claim);
            nextId = id + 1; // make sure next id is actually the next id...
            Logger.debug("   Claim " + id + " loaded successfully.");
            count++;
        }

        Logger.info("Finished loading all claims! (total: " + count + " / " + total + ")");
    }

    public void unloadClaims() {
        claims.clear();
        ClaimConfig.removeAll();
    }
}
