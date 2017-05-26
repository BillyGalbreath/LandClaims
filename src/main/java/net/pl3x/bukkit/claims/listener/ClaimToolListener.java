package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.Coordinates;
import net.pl3x.bukkit.claims.configuration.ClaimConfig;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.event.CreateClaimEvent;
import net.pl3x.bukkit.claims.event.InspectClaimsEvent;
import net.pl3x.bukkit.claims.event.ResizeClaimEvent;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.player.ToolMode;
import net.pl3x.bukkit.claims.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

public class ClaimToolListener implements Listener {
    private final Pl3xClaims plugin;

    public ClaimToolListener(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // only care about main hand packet
        }

        Player player = event.getPlayer();
        if (Config.isWorldDisabled(player.getWorld())) {
            return; // claims not enabled in this world
        }

        Block clickedBlock = null;
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            // get block looking at (for extended reach)
            clickedBlock = player.getTargetBlock((HashSet<Material>) null, 100);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // get clicked block
            clickedBlock = event.getClickedBlock();
        }

        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player);
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            if (Config.isInspectTool(player.getInventory().getItemInMainHand())) {
                Lang.send(player, Lang.INSPECT_TOO_FAR);
                pl3xPlayer.revertVisualization();
            }
            return; // no block clicked
        }
        if (Config.isInspectTool(player.getInventory().getItemInMainHand())) {
            if (!player.hasPermission("claims.visualize")) {
                return;
            }

            if (player.isSneaking() && player.hasPermission("claims.visualize.nearby")) {
                // is inspecting all nearby claims
                Collection<Claim> nearbyClaims = plugin.getClaimManager().getNearbyClaims(player.getLocation());

                InspectClaimsEvent inspectClaimsEvent = new InspectClaimsEvent(player, nearbyClaims);
                Bukkit.getPluginManager().callEvent(inspectClaimsEvent);
                if (inspectClaimsEvent.isCancelled()) {
                    return; // cancelled by plugin
                }

                if (nearbyClaims == null || nearbyClaims.isEmpty()) {
                    Lang.send(player, Lang.INSPECT_NO_CLAIM);

                    pl3xPlayer.revertVisualization();
                    return;
                }

                Lang.send(player, Lang.INSPECT_NEARBY_CLAIMS
                        .replace("{amount}", Integer.toString(nearbyClaims.size())));
                pl3xPlayer.showVisualization(nearbyClaims);
                return;
            }

            // is inspecting a single claim
            Claim claim = plugin.getClaimManager().getClaim(clickedBlock.getLocation());

            InspectClaimsEvent inspectClaimsEvent = new InspectClaimsEvent(player, claim);
            Bukkit.getPluginManager().callEvent(inspectClaimsEvent);
            if (inspectClaimsEvent.isCancelled()) {
                return; // cancelled by plugin
            }

            if (claim == null) {
                Lang.send(player, Lang.INSPECT_NO_CLAIM);
                pl3xPlayer.revertVisualization();
                return;
            }

            Lang.send(player, Lang.INSPECT_BLOCK_CLAIMED
                    .replace("{owner}", claim.getOwner() == null ? "admin" : Bukkit.getOfflinePlayer(claim.getOwner()).getName()));
            pl3xPlayer.showVisualization(claim);

            if (player.hasPermission("claims.inspect.seeclaimsize")) {
                Lang.send(player, Lang.INSPECT_CLAIM_DIMENSIONS
                        .replace("{widthX}", Integer.toString(claim.getCoordinates().getWidthX()))
                        .replace("{widthZ}", Integer.toString(claim.getCoordinates().getWidthZ()))
                        .replace("{area}", Integer.toString(claim.getCoordinates().getArea())));
            }

            if (!claim.isAdminClaim() &&
                    (player.hasPermission("command.deleteclaims") ||
                            player.hasPermission("claims.inspect.seeinactivity"))) {
                Lang.send(player, Lang.INSPECT_OWNER_INACTIVITY
                        .replace("{amount}", Long.toString((new Date().getTime() -
                                new Date(Bukkit.getOfflinePlayer(claim.getParent() != null ?
                                        claim.getParent().getOwner() :
                                        claim.getOwner()).getLastPlayed())
                                        .getTime()) / 86400000)));
            }
            return;
        }

        if (Config.isClaimTool(player.getInventory().getItemInMainHand())) {
            if (!player.hasPermission("command.claim")) {
                Lang.send(player, Lang.NO_CLAIM_CREATE_PERMISSION);
                return; // no permission to create/resize claims
            }

            // resizing claim that hasn't been deleted since started resizing
            Claim clickedClaim = plugin.getClaimManager().getClaim(clickedBlock.getLocation());
            if (pl3xPlayer.getResizingClaim() != null && plugin.getClaimManager().getClaim(pl3xPlayer.getResizingClaim().getId()) != null) {
                if (clickedBlock.getLocation().equals(pl3xPlayer.getLastToolLocation())) {
                    return; // clicking the same location
                }

                Location toolLoc = pl3xPlayer.getLastToolLocation();
                Coordinates coords = pl3xPlayer.getResizingClaim().getCoordinates();
                int minX = toolLoc.getX() == coords.getMinX() ? clickedBlock.getX() : coords.getMinX();
                int maxX = toolLoc.getX() == coords.getMaxX() ? clickedBlock.getX() : coords.getMaxX();
                int minZ = toolLoc.getZ() == coords.getMinZ() ? clickedBlock.getZ() : coords.getMinZ();
                int maxZ = toolLoc.getZ() == coords.getMaxZ() ? clickedBlock.getZ() : coords.getMaxZ();

                int newWidthX = maxX - minX + 1;
                int newWidthZ = maxZ - minZ + 1;
                int newArea = newWidthX * newWidthZ;

                // check top level claim size rules and permissions
                // admin claims bypass this check
                if (pl3xPlayer.getResizingClaim().getParent() == null && !pl3xPlayer.getResizingClaim().isAdminClaim()) {
                    // check minimum size requirements if shrinking
                    // players with "adminclaims" permissions bypass this check
                    if (!player.hasPermission("command.adminclaims") &&
                            (newWidthX < coords.getWidthX() || newWidthZ < coords.getWidthZ())) {
                        if (newWidthX < Config.CLAIM_MIN_WIDTH || newWidthZ < Config.CLAIM_MIN_WIDTH) {
                            Lang.send(player, Lang.RESIZE_FAILED_TOO_NARROW
                                    .replace("{minimum}", Integer.toString(Config.CLAIM_MIN_WIDTH)));
                            return;
                        }
                        if (newArea < Config.CLAIM_MIN_AREA) {
                            Lang.send(player, Lang.RESIZE_FAILED_TOO_SMALL
                                    .replace("{minimum}", Integer.toString(Config.CLAIM_MIN_AREA)));
                            return;
                        }
                    }

                    // check if player has enough claim blocks
                    if (pl3xPlayer.getResizingClaim().isOwner(player)) {
                        int remaining = pl3xPlayer.getRemainingClaimBlocks() + coords.getArea() - newArea;
                        if (remaining < 0) {
                            Lang.send(player, Lang.RESIZE_FAILED_NEED_MORE_BLOCKS
                                    .replace("{amount}", Integer.toString(-remaining)));
                            return;
                        }
                    }
                }

                Coordinates newCoords = new Coordinates(coords.getWorld(), minX, maxX, minZ, maxZ);
                for (Claim topLevelClaim : plugin.getClaimManager().getTopLevelClaims()) {
                    if (topLevelClaim == pl3xPlayer.getResizingClaim()) {
                        continue;
                    }
                    if (topLevelClaim.getCoordinates().overlaps(newCoords)) {
                        Lang.send(player, Lang.RESIZE_FAILED_OVERLAP);
                        pl3xPlayer.showVisualization(topLevelClaim, VisualizationType.ERROR);
                        return;
                    }
                }

                ResizeClaimEvent resizeClaimEvent = new ResizeClaimEvent(player, pl3xPlayer.getResizingClaim());
                Bukkit.getPluginManager().callEvent(resizeClaimEvent);
                if (resizeClaimEvent.isCancelled()) {
                    return; // cancelled by plugin
                }

                // resize the claim
                coords.resize(minX, maxX, minZ, maxZ);
                ClaimConfig claimConfig = ClaimConfig.getConfig(plugin, pl3xPlayer.getResizingClaim().getId());
                claimConfig.setCoordinates(coords);
                claimConfig.save();

                // calculate remaining claim blocks (for display purposes)
                int remainingClaimBlocks = pl3xPlayer.getRemainingClaimBlocks();
                UUID owner = pl3xPlayer.getResizingClaim().getParent() != null ? pl3xPlayer.getResizingClaim().getParent().getOwner() : pl3xPlayer.getResizingClaim().getOwner();
                if (!player.getUniqueId().equals(owner)) {
                    plugin.getPlayerManager().getPlayer(owner).getRemainingClaimBlocks();
                    if (!Bukkit.getOfflinePlayer(owner).isOnline()) {
                        plugin.getPlayerManager().unload(owner);
                    }
                }

                Lang.send(player, Lang.RESIZE_SUCCESS
                        .replace("{amount}", Integer.toString(remainingClaimBlocks)));
                pl3xPlayer.showVisualization(pl3xPlayer.getResizingClaim());

                pl3xPlayer.setLastToolLocation(null);
                pl3xPlayer.setResizingClaim(null);
                return;
            }
            // end resizing claim

            // must be starting a resize, creating a new claim, or creating a child

            // clicked inside existing claim, not creating a new one
            if (clickedClaim != null) {
                if (!clickedClaim.allowEdit(player)) {
                    Lang.send(player, Lang.CREATE_FAILED_OVERLAP_OTHER_PLAYER
                            .replace("{owner}", Bukkit.getOfflinePlayer(clickedClaim.getOwner()).getName()));
                    pl3xPlayer.showVisualization(clickedClaim, VisualizationType.ERROR);
                    return;
                }

                // clicked on a corner, start resizing
                if (clickedClaim.getCoordinates().isCorner(clickedBlock.getLocation())) {
                    pl3xPlayer.setResizingClaim(clickedClaim);
                    pl3xPlayer.setLastToolLocation(clickedBlock.getLocation());
                    Lang.send(player, Lang.RESIZE_START);
                    return;
                }

                // did not click on corner and in child mode, creating a new child
                if (pl3xPlayer.getToolMode() == ToolMode.CHILD) {
                    // first click, starting new child
                    if (pl3xPlayer.getLastToolLocation() == null) {
                        if (clickedClaim.getParent() != null) {
                            Lang.send(player, Lang.CREATE_FAILED_CHILD);
                            return;
                        }
                        pl3xPlayer.setLastToolLocation(clickedBlock.getLocation());
                        pl3xPlayer.setParentClaim(clickedClaim);
                        Lang.send(player, Lang.CREATE_START_CHILD);
                        return;
                    }

                    // clicked in another world
                    if (!pl3xPlayer.getLastToolLocation().getWorld().equals(clickedBlock.getWorld())) {
                        pl3xPlayer.setLastToolLocation(null);
                        onPlayerInteract(event);
                        return;
                    }

                    // finish creating child
                    Coordinates newChildCoords = new Coordinates(pl3xPlayer.getLastToolLocation(), clickedBlock.getLocation());

                    // check if enough claim blocks

                    // check if child fits completely inside parent
                    if (!pl3xPlayer.getParentClaim().getCoordinates().contains(newChildCoords)) {
                        Lang.send(player, Lang.CREATE_FAILED_CHILD_OVERLAP_PARENT);
                        pl3xPlayer.showVisualization(pl3xPlayer.getParentClaim(), VisualizationType.ERROR);
                        return;
                    }

                    // check if overlapping other child claims
                    for (Claim siblingClaim : pl3xPlayer.getParentClaim().getChildren()) {
                        if (siblingClaim == clickedClaim) {
                            continue;
                        }
                        if (siblingClaim.getCoordinates().overlaps(newChildCoords)) {
                            Lang.send(player, Lang.CREATE_FAILED_CHILD_OVERLAP);
                            pl3xPlayer.showVisualization(siblingClaim, VisualizationType.ERROR);
                            return;
                        }
                    }

                    Claim newChildClaim = new Claim(plugin.getClaimManager().getNextId(), null, // child claims have no owner
                            pl3xPlayer.getParentClaim(), newChildCoords, false);

                    CreateClaimEvent createClaimEvent = new CreateClaimEvent(player, newChildClaim);
                    Bukkit.getPluginManager().callEvent(createClaimEvent);
                    if (createClaimEvent.isCancelled()) {
                        return; // cancelled by plugin
                    }

                    // save the new child
                    pl3xPlayer.getParentClaim().addChild(newChildClaim);
                    ClaimConfig claimConfig = ClaimConfig.getConfig(plugin, newChildClaim.getId());
                    claimConfig.setCoordinates(newChildCoords);
                    claimConfig.save();

                    Lang.send(player, Lang.CREATE_SUCCESS_CHILD);
                    pl3xPlayer.showVisualization(newChildClaim);

                    pl3xPlayer.setLastToolLocation(null);
                    pl3xPlayer.setParentClaim(null);
                    return;
                }

                // overlapping parent claim
                Lang.send(player, Lang.CREATE_FAILED_OVERLAP);
                pl3xPlayer.showVisualization(clickedClaim, VisualizationType.ERROR);
                return;
            }
            // end click inside existing claim

            // clicked outside any claims

            // start first click
            Location toolLocation = pl3xPlayer.getLastToolLocation();
            if (toolLocation == null) {
                if (Config.MAX_CLAIMS_PER_PLAYER > 0 &&
                        !player.hasPermission("claims.overridelimits") &&
                        pl3xPlayer.getClaims().size() > Config.MAX_CLAIMS_PER_PLAYER) {
                    Lang.send(player, Lang.CREATE_FAILED_CLAIM_LIMIT
                            .replace("{limit}", Integer.toString(Config.MAX_CLAIMS_PER_PLAYER)));
                    return;
                }

                pl3xPlayer.setLastToolLocation(clickedBlock.getLocation());
                Lang.send(player, Lang.CREATE_START);

                pl3xPlayer.showVisualization(new Claim(-99, null, null,
                                new Coordinates(pl3xPlayer.getLastToolLocation(), clickedBlock.getLocation()), false),
                        VisualizationType.NEW_POINT);
                return;
            }
            // end first click

            // finish second click

            // clicked in another world
            if (!pl3xPlayer.getLastToolLocation().getWorld().equals(clickedBlock.getWorld())) {
                pl3xPlayer.setLastToolLocation(null);
                onPlayerInteract(event);
                return;
            }

            // check dimensions and if player has enough claim blocks
            boolean isAdminClaim = pl3xPlayer.getToolMode() == ToolMode.ADMIN;
            Coordinates newCoords = new Coordinates(pl3xPlayer.getLastToolLocation(), clickedBlock.getLocation());
            if (!isAdminClaim) {
                if (newCoords.getWidthX() < Config.CLAIM_MIN_WIDTH ||
                        newCoords.getWidthZ() < Config.CLAIM_MIN_WIDTH) {
                    Lang.send(player, Lang.CREATE_FAILED_TOO_NARROW
                            .replace("{minimum}", Integer.toString(Config.CLAIM_MIN_WIDTH)));
                    return;
                }
                if (newCoords.getArea() < Config.CLAIM_MIN_AREA) {
                    Lang.send(player, Lang.CREATE_FAILED_TOO_SMALL
                            .replace("{minimum}", Integer.toString(Config.CLAIM_MIN_AREA)));
                    return;
                }

                int remainingBlocks = pl3xPlayer.getRemainingClaimBlocks();
                if (newCoords.getArea() > remainingBlocks) {
                    Lang.send(player, Lang.CREATE_FAILED_NEED_MORE_BLOCKS
                            .replace("{required}", Integer.toString(newCoords.getArea() - remainingBlocks)));
                    return;
                }
            }

            // check for overlaps
            for (Claim topLevelClaim : plugin.getClaimManager().getTopLevelClaims()) {
                if (topLevelClaim.getCoordinates().overlaps(newCoords)) {
                    Lang.send(player, Lang.CREATE_FAILED_OVERLAP);
                    pl3xPlayer.showVisualization(topLevelClaim, VisualizationType.ERROR);
                    return;
                }
            }

            // create new claim
            Claim newClaim = new Claim(plugin.getClaimManager().getNextId(),
                    (isAdminClaim ? null : player.getUniqueId()),
                    null, newCoords, isAdminClaim);

            CreateClaimEvent createClaimEvent = new CreateClaimEvent(player, newClaim);
            Bukkit.getPluginManager().callEvent(createClaimEvent);
            if (createClaimEvent.isCancelled()) {
                return; // cancelled by plugin
            }

            plugin.getClaimManager().createNewClaim(newClaim);
            Lang.send(player, Lang.CREATE_SUCCESS
                    .replace("{amount}", Integer.toString(pl3xPlayer.getRemainingClaimBlocks())));

            pl3xPlayer.showVisualization(newClaim);

            pl3xPlayer.setLastToolLocation(null);
            // end second click
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (Config.isWorldDisabled(player.getWorld())) {
            return; // claims not enabled in this world
        }

        if (!Config.isClaimTool(player.getInventory().getItem(event.getNewSlot()))) {
            return; // not switching to claim tool
        }

        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player);
        pl3xPlayer.setLastToolLocation(null);
        pl3xPlayer.setResizingClaim(null);

        if (pl3xPlayer.getToolMode() != ToolMode.BASIC) {
            pl3xPlayer.setToolMode(ToolMode.BASIC);
            Lang.send(player, Lang.TOOLMODE_BASIC);
        }

        Lang.send(player, Lang.REMAINING_CLAIM_BLOCKS
                .replace("{amount}", Integer.toString(pl3xPlayer.getRemainingClaimBlocks())));

        Claim claim = plugin.getClaimManager().getClaim(player.getLocation());
        if (claim != null && claim.allowEdit(player)) {
            pl3xPlayer.showVisualization(claim);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
        pl3xPlayer.setLastToolLocation(null);
        pl3xPlayer.setParentClaim(null);
        pl3xPlayer.setToolMode(ToolMode.BASIC);
        pl3xPlayer.setVisualization(null);
        pl3xPlayer.setResizingClaim(null);
    }
}
