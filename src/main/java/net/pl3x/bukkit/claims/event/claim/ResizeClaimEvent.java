package net.pl3x.bukkit.claims.event.claim;

import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.Coordinates;
import net.pl3x.bukkit.claims.event.ClaimEvent;
import org.bukkit.entity.Player;

public class ResizeClaimEvent extends ClaimEvent {
    private Coordinates newCoords;

    public ResizeClaimEvent(Player player, Claim claim, Coordinates newCoords) {
        super(player, claim);
        this.newCoords = newCoords;
    }

    public Coordinates getNewCoords() {
        return newCoords;
    }

    public void setNewCoords(Coordinates newCoords) {
        this.newCoords = newCoords;
    }
}
