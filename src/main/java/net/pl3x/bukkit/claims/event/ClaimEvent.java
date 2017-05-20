package net.pl3x.bukkit.claims.event;

import net.pl3x.bukkit.claims.claim.Claim;
import org.bukkit.entity.Player;

public abstract class ClaimEvent extends CancellableEvent {
    private final Claim claim;

    public ClaimEvent(Player player, Claim claim) {
        super(player);
        this.claim = claim;
    }

    /**
     * Get the claim involved in this event
     *
     * @return Claim
     */
    public Claim getClaim() {
        return claim;
    }
}
