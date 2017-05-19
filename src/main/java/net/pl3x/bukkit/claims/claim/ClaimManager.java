package net.pl3x.bukkit.claims.claim;

import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClaimManager {
    private static final ClaimManager instance = new ClaimManager();

    public static ClaimManager getInstance() {
        return instance;
    }

    private ClaimManager() {
    }

    private final Map<Long, Claim> claims = new HashMap<>();

    public Collection<Claim> getClaims() {
        return claims.values();
    }

    public Claim getClaim(long id) {
        return claims.get(id);
    }

    public Claim getClaim(Location location) {
        for (Claim claim : getClaims()) {
            if (claim.contains(location)) {
                return claim;
            }
        }
        return null;
    }

    public void addClaim(Claim claim) {
        claims.put(claim.getId(), claim);
    }

    public void loadClaims() {
        unloadClaims();
        //
    }

    public void unloadClaims() {
        claims.clear();
    }
}
