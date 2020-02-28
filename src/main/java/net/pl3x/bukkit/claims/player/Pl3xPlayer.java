package net.pl3x.bukkit.claims.player;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.configuration.PlayerConfig;
import net.pl3x.bukkit.claims.event.claim.VisualizeClaimsEvent;
import net.pl3x.bukkit.claims.event.player.PlayerChangedClaimEvent;
import net.pl3x.bukkit.claims.player.task.AccrueClaimBlocksTask;
import net.pl3x.bukkit.claims.visualization.Visualization;
import net.pl3x.bukkit.claims.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Pl3xPlayer extends PlayerConfig {
    private final LandClaims plugin;
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
    private Set<Long> entryMessageOnCooldown = new HashSet<>();
    private Set<Long> exitMessageOnCooldown = new HashSet<>();

    Pl3xPlayer(LandClaims plugin, OfflinePlayer player) {
        super(plugin, player.getUniqueId());
        this.plugin = plugin;
        this.player = player;

        accrueClaimBlocksTask = new AccrueClaimBlocksTask(this);
        accrueClaimBlocksTask.runTaskTimer(plugin, 1200, 1200); // 1 minute cycle
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

    public LandClaims getPlugin() {
        return plugin;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public double getClaimBlocks() {
        return getDouble("claim-blocks", 0);
    }

    public void setClaimBlocks(double claimBlocks) {
        set("claim-blocks", claimBlocks);
        save();
    }

    public double getBonusBlocks() {
        return getDouble("bonus-blocks", 0);
    }

    public void setBonusBlocks(double bonusBlocks) {
        set("bonus-blocks", bonusBlocks);
        save();
    }

    public int getRemainingClaimBlocks() {
        double total = getClaimBlocks() + getBonusBlocks();
        for (Claim claim : getClaims()) {
            total -= claim.getCoordinates().getArea();
        }
        return (int) total;
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

        if (player.isOnline()) {
            new PlayerChangedClaimEvent(player.getPlayer(), claim, inClaim).callEvent();
            if (inClaim != null && inClaim.hasExitMessage() && !exitMessageOnCooldown.contains(inClaim.getId())) {
                Lang.send(player.getPlayer(), inClaim.getExitMessage().replace("\\n", "\n"));
                long id = inClaim.getId();
                exitMessageOnCooldown.add(id);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        exitMessageOnCooldown.remove(id);
                    }
                }.runTaskLater(plugin, 6000);
            }
            if (claim != null && claim.hasEntryMessage() && !entryMessageOnCooldown.contains(claim.getId())) {
                Lang.send(player.getPlayer(), claim.getEntryMessage().replace("\\n", "\n"));
                long id = claim.getId();
                entryMessageOnCooldown.add(id);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entryMessageOnCooldown.remove(id);
                    }
                }.runTaskLater(plugin, 6000);
            }
        }
        this.inClaim = claim;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void updateLocation(Location newLocation) {
        if (player.isOnline()) {
            this.lastLocation = newLocation.clone();
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
