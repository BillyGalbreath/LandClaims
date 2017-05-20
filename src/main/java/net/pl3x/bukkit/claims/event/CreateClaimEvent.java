package net.pl3x.bukkit.claims.event;

import net.pl3x.bukkit.claims.claim.Claim;
import org.bukkit.entity.Player;

public class CreateClaimEvent extends ClaimEvent {
    private final boolean isAdminClaim;

    public CreateClaimEvent(Player player, Claim claim, boolean isAdminClaim) {
        super(player, claim);
        this.isAdminClaim = isAdminClaim;
    }

    public boolean isAdminClaim() {
        return isAdminClaim;
    }
}
