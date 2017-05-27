package net.pl3x.bukkit.claims.event.claim;

import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.event.ClaimEvent;
import org.bukkit.entity.Player;

public class CreateClaimEvent extends ClaimEvent {
    public CreateClaimEvent(Player player, Claim claim) {
        super(player, claim);
    }
}
