package net.pl3x.bukkit.claims.event.claim;

import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.event.CancellableEvent;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class InspectClaimsEvent extends CancellableEvent {
    private final Collection<Claim> claims;

    public InspectClaimsEvent(Player player, Claim claim) {
        super(player);
        this.claims = Collections.singleton(claim);
    }

    public InspectClaimsEvent(Player player, Collection<Claim> claims) {
        super(player);
        this.claims = claims;
    }

    /**
     * Get the claims being inspected
     *
     * @return Claims
     */
    public Collection<Claim> getClaims() {
        return claims;
    }
}
