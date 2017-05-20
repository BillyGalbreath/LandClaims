package net.pl3x.bukkit.claims.event;

import net.pl3x.bukkit.claims.claim.Claim;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class VisualizeClaimsEvent extends CancellableEvent {
    private final Collection<Claim> claims;
    private final boolean showChildren;

    public VisualizeClaimsEvent(Player player, Claim claim) {
        super(player);
        this.claims = Collections.singleton(claim);
        this.showChildren = true;
    }

    public VisualizeClaimsEvent(Player player, Collection<Claim> claims) {
        super(player);
        this.claims = claims;
        this.showChildren = false;
    }

    /**
     * Get the claims being visualized, or null if visualization being removed
     *
     * @return Claims
     */
    public Collection<Claim> getClaims() {
        return claims;
    }

    /**
     * Check if child claims are being shown
     *
     * @return True if child claims are being shown
     */
    public boolean isShowChildren() {
        return showChildren;
    }
}
