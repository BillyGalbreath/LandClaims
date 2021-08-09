package net.pl3x.bukkit.claims.hook;

import de.Linus122.SafariNet.API.Listener;
import de.Linus122.SafariNet.API.SafariNet;
import de.Linus122.SafariNet.API.Status;
import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SafariNetHook {
    public SafariNetHook() {
        SafariNet.addListener(new Listener() {
            @Override
            public void playerCatchEntity(Player player, Entity entity, Status status) {
                Claim claim = LandClaims.getInstance().getClaimManager().getClaim(entity.getLocation());
                if (claim != null && !claim.allowContainers(player)) {
                    status.setCancelled(true);
                }
            }

            @Override
            public void playerReleaseEntity(Player player, Entity entity, Status status) {
                Claim claim = LandClaims.getInstance().getClaimManager().getClaim(entity.getLocation());
                if (claim != null && !claim.allowContainers(player)) {
                    status.setCancelled(true);
                }
            }
        });
    }
}
