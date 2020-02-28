package net.pl3x.bukkit.claims.event.player;

import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.event.ClaimEvent;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PlayerChangedClaimEvent extends ClaimEvent {
    private final Claim oldClaim;

    public PlayerChangedClaimEvent(Player player, Claim oldClaim, Claim newClaim) {
        super(player, newClaim);
        this.oldClaim = oldClaim;
    }

    /**
     * Get the claim the player is entering
     *
     * @return Claim player is entering
     */
    @Override
    @Nullable
    public Claim getClaim() {
        return super.getClaim();
    }

    /**
     * Get the claim the player is entering
     *
     * @return Claim player is entering
     */
    @Nullable
    public Claim getNewClaim() {
        return getClaim();
    }

    /**
     * Get the claim the player is leaving
     *
     * @return Claim player is leaving
     */
    @Nullable
    public Claim getOldClaim() {
        return oldClaim;
    }
}
