package net.pl3x.bukkit.claims.player;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.tool.BasicClaimTool;
import net.pl3x.bukkit.claims.claim.tool.ClaimTool;
import net.pl3x.bukkit.claims.configuration.PlayerConfig;
import net.pl3x.bukkit.claims.event.VisualizeClaimsEvent;
import net.pl3x.bukkit.claims.visualization.Visualization;
import net.pl3x.bukkit.claims.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class Pl3xPlayer extends PlayerConfig {
    private Pl3xClaims plugin;
    private Player player;
    private Location lastLocation;
    private ClaimTool claimTool = new BasicClaimTool();
    private Claim inClaim;
    private Visualization visualization;

    Pl3xPlayer(Pl3xClaims plugin, Player player) {
        super(plugin, player.getUniqueId());
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * Use Pl3xPlayer#unload(Player) instead
     */
    void unload() {
        player = null;
        lastLocation = null;
        claimTool = null;
        inClaim = null;
    }

    public Player getPlayer() {
        return player;
    }

    public int getClaimBlocks() {
        return getInt("claim-blocks", 0);
    }

    public void setClaimBlocks(int claimBlocks) {
        set("claim-blocks", claimBlocks);
        save();
    }

    public int getBonusBlocks() {
        return getInt("bonus-blocks", 0);
    }

    public void setBonusBlocks(int bonusBlocks) {
        set("bonus-blocks", bonusBlocks);
        save();
    }

    public Claim inClaim() {
        return inClaim;
    }

    public void inClaim(Claim claim) {
        if (claim == inClaim) {
            return;
        }

        this.inClaim = claim;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void updateLocation() {
        this.lastLocation = player.getLocation();
        inClaim(plugin.getClaimManager().getClaim(lastLocation));
    }

    public ClaimTool getClaimTool() {
        return claimTool;
    }

    public void setClaimTool(ClaimTool claimTool) {
        this.claimTool = claimTool;
        revertVisualization();
    }

    public Visualization getVisualization() {
        return visualization;
    }

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;
    }

    public void revertVisualization() {
        showVisualization((Collection<Claim>) null, null);
    }

    public void showVisualization(Claim claim) {
        showVisualization(Collections.singleton(claim));
    }

    public void showVisualization(Collection<Claim> claims) {
        showVisualization(claims, VisualizationType.CLAIM);
    }

    public void showVisualization(Claim claim, VisualizationType type) {
        showVisualization(Collections.singleton(claim), type);
    }

    public void showVisualization(Collection<Claim> claims, VisualizationType type) {
        if (claims == null || claims.isEmpty()) {
            if (visualization == null) {
                return; // nothing to revert
            }
            VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player, null, null);
            Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
            if (!visualizeClaimsEvent.isCancelled()) {
                visualization.revert(player);
            }
        } else {
            Visualization visualization = new Visualization(plugin, claims, type, player.getEyeLocation());
            VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player, claims, visualization);
            Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
            if (!visualizeClaimsEvent.isCancelled()) {
                visualization.apply(plugin, player);
            }
        }
    }
}
