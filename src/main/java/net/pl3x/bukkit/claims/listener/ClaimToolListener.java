package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.Coordinates;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.event.claim.CreateClaimEvent;
import net.pl3x.bukkit.claims.event.claim.InspectClaimsEvent;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class ClaimToolListener implements Listener {
    private final Pl3xClaims plugin;

    public ClaimToolListener(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInspectTool(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // only care about main hand packet
        }

        Player player = event.getPlayer();
        if (Config.isWorldDisabled(player.getWorld())) {
            return; // claims not enabled in this world
        }

        if (!player.hasPermission("claims.visualize")) {
            return;
        }

        if (!Config.isInspectTool(player.getInventory().getItemInMainHand())) {
            return;
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
            Lang.send(player, Lang.INSPECT_TOO_FAR);
            pl3xPlayer.revertVisualization();
            return; // no block clicked
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
                .replace("{owner}", claim.getOwnerName())
                .replace("{flags}", claim.getFlagsList()));

        // if this claim has children lets show them too
        Claim showClaim = claim;
        if (!claim.getChildren().isEmpty()) {
            showClaim = claim.getChildren().stream().findFirst().orElse(claim);
        }
        pl3xPlayer.showVisualization(showClaim);

        if (player.hasPermission("claims.inspect.seeclaimsize")) {
            Lang.send(player, Lang.INSPECT_CLAIM_DIMENSIONS
                    .replace("{type}", claim.getParent() == null ? "Top Level" : "Child")
                    .replace("{widthX}", Integer.toString(claim.getCoordinates().getWidthX()))
                    .replace("{widthZ}", Integer.toString(claim.getCoordinates().getWidthZ()))
                    .replace("{area}", Integer.toString(claim.getCoordinates().getArea())));
        }

        if (!claim.isAdminClaim() &&
                (player.hasPermission("command.deleteclaim") ||
                        player.hasPermission("claims.inspect.seeinactivity"))) {
            Lang.send(player, Lang.INSPECT_OWNER_INACTIVITY
                    .replace("{amount}", Long.toString((new Date().getTime() -
                            new Date(Bukkit.getOfflinePlayer(claim.getParent() != null ?
                                    claim.getParent().getOwner() :
                                    claim.getOwner()).getLastPlayed())
                                    .getTime()) / 86400000)));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerClaimTool(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // only care about main hand packet
        }

        Player player = event.getPlayer();
        if (Config.isWorldDisabled(player.getWorld())) {
            return; // claims not enabled in this world
        }

        if (!player.hasPermission("command.claim")) {
            return; // no permission to create/resize claims
        }

        if (!Config.isClaimTool(player.getInventory().getItemInMainHand())) {
            return;
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
            return; // no block clicked
        }

        // finish resizing claim that hasn't been deleted since started resizing
        Claim resizingClaim = pl3xPlayer.getResizingClaim();
        if (resizingClaim != null && (plugin.getClaimManager().getClaim(resizingClaim.getId()) != null ||
                (resizingClaim.getParent() != null && resizingClaim.getParent().getChildren().contains(resizingClaim)))) {
            if (clickedBlock.getLocation().equals(pl3xPlayer.getLastToolLocation())) {
                return; // clicking the same location
            }

            Location toolLoc = pl3xPlayer.getLastToolLocation();
            Coordinates oldCoords = resizingClaim.getCoordinates();

            plugin.getClaimManager().resizeClaim(player, resizingClaim,
                    new Coordinates(oldCoords.getWorld(),
                            toolLoc.getX() == oldCoords.getMinX() ? clickedBlock.getX() : oldCoords.getMinX(),
                            toolLoc.getZ() == oldCoords.getMinZ() ? clickedBlock.getZ() : oldCoords.getMinZ(),
                            toolLoc.getX() == oldCoords.getMaxX() ? clickedBlock.getX() : oldCoords.getMaxX(),
                            toolLoc.getZ() == oldCoords.getMaxZ() ? clickedBlock.getZ() : oldCoords.getMaxZ()));
            return;
        }
        // end finish resizing claim

        // must be starting a resize, creating a new claim, or creating a child

        // clicked inside existing claim, not creating a new one
        Claim clickedClaim = plugin.getClaimManager().getClaim(clickedBlock.getLocation());
        if (clickedClaim != null) {
            if (!clickedClaim.allowEdit(player)) {
                Lang.send(player, Lang.CREATE_FAILED_OVERLAP_OTHER_PLAYER
                        .replace("{owner}", clickedClaim.getOwnerName()));
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
                    onPlayerClaimTool(event);
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
                    if (siblingClaim.getId() == clickedClaim.getId()) {
                        continue;
                    }
                    if (siblingClaim.getCoordinates().overlaps(newChildCoords)) {
                        Lang.send(player, Lang.CREATE_FAILED_CHILD_OVERLAP);
                        pl3xPlayer.showVisualization(siblingClaim, VisualizationType.ERROR);
                        return;
                    }
                }

                Claim parent = pl3xPlayer.getParentClaim();
                Claim newChildClaim = new Claim(plugin, plugin.getClaimManager().getNextId(), player.getUniqueId(), // child claims have no owner
                        parent, newChildCoords, false);

                CreateClaimEvent createClaimEvent = new CreateClaimEvent(player, newChildClaim);
                Bukkit.getPluginManager().callEvent(createClaimEvent);
                if (createClaimEvent.isCancelled()) {
                    return; // cancelled by plugin
                }

                // save the new child
                parent.addChild(newChildClaim);
                plugin.getClaimManager().createNewClaim(newChildClaim);

                Lang.send(player, Lang.CREATE_SUCCESS_CHILD);
                pl3xPlayer.showVisualization(newChildClaim, VisualizationType.CLAIM);

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
            if (Config.CLAIMS_MAX_PER_PLAYER > 0 &&
                    !player.hasPermission("claims.overridelimits") &&
                    pl3xPlayer.getClaims().size() > Config.CLAIMS_MAX_PER_PLAYER) {
                Lang.send(player, Lang.CREATE_FAILED_CLAIM_LIMIT
                        .replace("{limit}", Integer.toString(Config.CLAIMS_MAX_PER_PLAYER)));
                return;
            }

            pl3xPlayer.setLastToolLocation(clickedBlock.getLocation());
            Lang.send(player, Lang.CREATE_START);

            pl3xPlayer.showVisualization(new Claim(plugin, -99, null, null,
                            new Coordinates(pl3xPlayer.getLastToolLocation(), clickedBlock.getLocation()), false),
                    VisualizationType.NEW_POINT);
            return;
        }
        // end first click

        // finish second click

        // clicked in another world
        if (!pl3xPlayer.getLastToolLocation().getWorld().equals(clickedBlock.getWorld())) {
            pl3xPlayer.setLastToolLocation(null);
            onPlayerClaimTool(event);
            return;
        }

        // check dimensions and if player has enough claim blocks
        boolean isAdminClaim = pl3xPlayer.getToolMode() == ToolMode.ADMIN;
        Coordinates newCoords = new Coordinates(pl3xPlayer.getLastToolLocation(), clickedBlock.getLocation());
        if (!isAdminClaim) {
            if (newCoords.getWidthX() < Config.CLAIMS_MIN_WIDTH ||
                    newCoords.getWidthZ() < Config.CLAIMS_MIN_WIDTH) {
                Lang.send(player, Lang.CREATE_FAILED_TOO_NARROW
                        .replace("{minimum}", Integer.toString(Config.CLAIMS_MIN_WIDTH)));
                return;
            }
            if (newCoords.getArea() < Config.CLAIMS_MIN_AREA) {
                Lang.send(player, Lang.CREATE_FAILED_TOO_SMALL
                        .replace("{minimum}", Integer.toString(Config.CLAIMS_MIN_AREA)));
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
        Claim newClaim = new Claim(plugin, plugin.getClaimManager().getNextId(),
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
            pl3xPlayer.showVisualization(claim.getChildren().stream().findFirst().orElse(claim));
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPlacedBlockOutsideClaim(BlockPlaceEvent event) {
        //
        // TODO show player their claim and warn about no protection
        //
    }
}
