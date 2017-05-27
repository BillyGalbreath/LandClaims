package net.pl3x.bukkit.claims.player;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.PlayerConfig;
import net.pl3x.bukkit.claims.event.VisualizeClaimsEvent;
import net.pl3x.bukkit.claims.player.task.AccrueClaimBlocksTask;
import net.pl3x.bukkit.claims.visualization.Visualization;
import net.pl3x.bukkit.claims.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Pl3xPlayer extends PlayerConfig {
    private final Pl3xClaims plugin;
    private OfflinePlayer player;
    private Location lastLocation;
    private Location lastIdleCheckLocation;
    private ToolMode toolMode = ToolMode.BASIC;
    private Claim resizingClaim;
    private Claim parentClaim;
    private Location lastToolLocation;
    private Claim inClaim;
    private boolean pendingRescue = false;
    private boolean ignoreClaims = false;
    private Visualization visualization;
    private AccrueClaimBlocksTask accrueClaimBlocksTask;

    Pl3xPlayer(Pl3xClaims plugin, OfflinePlayer player) {
        super(plugin, player.getUniqueId());
        this.plugin = plugin;
        this.player = player;

        accrueClaimBlocksTask = new AccrueClaimBlocksTask(this);
        accrueClaimBlocksTask.runTaskTimer(plugin, 12000, 12000); // 10 minute cycle
    }

    /**
     * Use Pl3xPlayer#unload(Player) instead
     */
    void unload() {
        player = null;
        lastLocation = null;
        lastIdleCheckLocation = null;
        resizingClaim = null;
        parentClaim = null;
        lastToolLocation = null;
        inClaim = null;
        visualization = null;

        if (accrueClaimBlocksTask != null) {
            accrueClaimBlocksTask.cancel();
            accrueClaimBlocksTask = null;
        }
    }

    public Pl3xClaims getPlugin() {
        return plugin;
    }

    public OfflinePlayer getPlayer() {
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

    public int getRemainingClaimBlocks() {
        int total = getClaimBlocks() + getBonusBlocks();
        for (Claim claim : getClaims()) {
            total -= claim.getCoordinates().getArea();
        }
        return total;
    }

    public Claim getResizingClaim() {
        return resizingClaim;
    }

    public void setResizingClaim(Claim claim) {
        this.resizingClaim = claim;
    }

    public Claim getParentClaim() {
        return parentClaim;
    }

    public void setParentClaim(Claim claim) {
        this.parentClaim = claim;
    }

    public Location getLastToolLocation() {
        return lastToolLocation;
    }

    public void setLastToolLocation(Location location) {
        this.lastToolLocation = location;
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
        if (player.isOnline()) {
            this.lastLocation = player.getPlayer().getLocation();
            inClaim(plugin.getClaimManager().getClaim(lastLocation));
        }
    }

    public Location getLastIdleCheckLocation() {
        return lastIdleCheckLocation;
    }

    public void updateLastIdleCheckLocation() {
        if (player.isOnline()) {
            this.lastIdleCheckLocation = player.getPlayer().getLocation();
        }
    }

    public ToolMode getToolMode() {
        return toolMode;
    }

    public void setToolMode(ToolMode toolMode) {
        this.toolMode = toolMode;
    }

    public boolean hasPendingRescue() {
        return pendingRescue;
    }

    public void setPendingRescue(boolean pendingRescue) {
        this.pendingRescue = pendingRescue;
    }

    public boolean isIgnoringClaims() {
        return ignoreClaims;
    }

    public void setIgnoreClaims(boolean ignoreClaims) {
        this.ignoreClaims = ignoreClaims;
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
        if (!player.isOnline()) {
            return;
        }
        if (claims == null || claims.isEmpty()) {
            if (visualization == null) {
                return; // nothing to revert
            }
            VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player.getPlayer(), null, null);
            Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
            if (!visualizeClaimsEvent.isCancelled()) {
                visualization.revert(player.getPlayer());
            }
        } else {
            Visualization visualization = new Visualization(plugin, claims, type, player.getPlayer().getEyeLocation());
            VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player.getPlayer(), claims, visualization);
            Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
            if (!visualizeClaimsEvent.isCancelled()) {
                visualization.apply(plugin, player.getPlayer());
            }
        }
    }

    public Collection<Claim> getClaims() {
        return plugin.getClaimManager().getTopLevelClaims().stream()
                .filter(topLevelClaim -> topLevelClaim.isOwner(player.getUniqueId()))
                .collect(Collectors.toCollection(HashSet::new));
    }
}
