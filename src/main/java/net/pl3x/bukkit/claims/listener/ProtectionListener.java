package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.flag.FlagType;
import net.pl3x.bukkit.claims.configuration.Config;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class ProtectionListener implements Listener {
    private final Pl3xClaims plugin;

    public ProtectionListener(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (!Config.isWorldEnabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (!event.getSource().getType().equals(Material.FIRE)) {
            return; // not fire that is spreading
        }

        Claim fromClaim = plugin.getClaimManager().getClaim(event.getSource().getLocation());
        Claim toClaim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());

        // stop fire spread if crossing border OR claim has firespread flag disabled
        if (fromClaim != toClaim || (toClaim != null && !toClaim.getFlag(FlagType.FIRESPREAD))) {
            event.setCancelled(true);
        }
    }
}
