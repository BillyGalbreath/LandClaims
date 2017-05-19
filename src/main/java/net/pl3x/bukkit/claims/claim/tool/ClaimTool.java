package net.pl3x.bukkit.claims.claim.tool;

import net.pl3x.bukkit.claims.claim.Claim;
import org.bukkit.Location;

public abstract class ClaimTool {
    private Location primary;
    private Location secondary;
    private Claim claim;

    public ClaimTool() {
        this(null, null);
    }

    public ClaimTool(Location primary) {
        this(primary, null);
    }

    public ClaimTool(Location primary, Location secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public Location getPrimary() {
        return primary;
    }

    public void setPrimary(Location primary) {
        this.primary = primary;
    }

    public Location getSecondary() {
        return secondary;
    }

    public void setSecondary(Location secondary) {
        this.secondary = secondary;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }
}
