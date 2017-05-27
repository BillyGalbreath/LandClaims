package net.pl3x.bukkit.claims.event.player;

import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.event.ClaimEvent;
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
