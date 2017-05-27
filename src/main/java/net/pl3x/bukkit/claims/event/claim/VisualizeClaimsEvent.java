package net.pl3x.bukkit.claims.event.claim;

import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.event.CancellableEvent;
import net.pl3x.bukkit.claims.visualization.Visualization;
import org.bukkit.entity.Player;

import java.util.Collection;

public class VisualizeClaimsEvent extends CancellableEvent {
    private final Visualization visualization;
    private final Collection<Claim> claims;
    private final boolean showChildren;

    public VisualizeClaimsEvent(Player player, Collection<Claim> claims, Visualization visualization) {
        super(player);
        this.claims = claims;
        this.visualization = visualization;

        // show child claims if this the only claim
        this.showChildren = !(claims != null && claims.size() > 0);
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
     * Get the visualization about to be shown, or null if reverting a player's current visualization
     *
     * @return Visualization to be shown
     */
    public Visualization getVisualization() {
        return visualization;
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
