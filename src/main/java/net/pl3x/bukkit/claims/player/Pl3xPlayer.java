package net.pl3x.bukkit.claims.player;

import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.ClaimManager;
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
import java.util.HashMap;
import java.util.Map;

public class Pl3xPlayer extends PlayerConfig {
    private static final Map<Player, Pl3xPlayer> players = new HashMap<>();

    public static Pl3xPlayer getPlayer(Player player) {
        if (!players.containsKey(player)) {
            players.put(player, new Pl3xPlayer(player));
        }
        return players.get(player);
    }

    public static void unload(Player player) {
        if (players.containsKey(player)) {
            players.get(player).unload();
            players.remove(player);
        }
    }

    public static void unloadAll() {
        players.values().forEach(Pl3xPlayer::unload);
        players.clear();
    }

    public static void reloadAll() {
        players.values().forEach(Pl3xPlayer::reload);
    }

    private Player player;
    private Location lastLocation;
    private ClaimTool claimTool = new BasicClaimTool();
    private Claim inClaim;
    private Visualization visualization;

    private Pl3xPlayer(Player player) {
        super(player.getUniqueId());
        this.player = player;
    }

    /**
     * Use Pl3xPlayer#unload(Player) instead
     */
    private void unload() {
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
        inClaim(ClaimManager.getInstance().getClaim(lastLocation));
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
            Visualization visualization = new Visualization(claims, type, player.getEyeLocation());
            VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player, claims, visualization);
            Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
            if (!visualizeClaimsEvent.isCancelled()) {
                visualization.apply(player);
            }
        }
    }
}
