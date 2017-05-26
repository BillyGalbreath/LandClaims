package net.pl3x.bukkit.claims.event;

import net.pl3x.bukkit.claims.claim.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SaveTrappedPlayerEvent extends ClaimEvent {
    private Location destination = null;

    public SaveTrappedPlayerEvent(Player player, Claim claim) {
        super(player, claim);
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }
}
