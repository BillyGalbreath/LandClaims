package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.Coordinates;
import net.pl3x.bukkit.claims.claim.tool.AdminClaimTool;
import net.pl3x.bukkit.claims.claim.tool.BasicClaimTool;
import net.pl3x.bukkit.claims.claim.tool.ChildClaimTool;
import net.pl3x.bukkit.claims.claim.tool.ClaimTool;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.event.CreateClaimEvent;
import net.pl3x.bukkit.claims.event.InspectClaimsEvent;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.visualization.VisualizationType;
import org.bukkit.Bukkit;
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
import java.util.HashSet;

public class ClaimToolListener implements Listener {
    private final Pl3xClaims plugin;

    public ClaimToolListener(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // only care about main hand packet
        }

        Player player = event.getPlayer();
        if (!Config.isWorldEnabled(player.getWorld())) {
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

                // TODO tell player how many claims
                //
                //

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

            // TODO tell player about the claim
            //
            //

            pl3xPlayer.showVisualization(claim);
            return;
        }

        if (Config.isClaimTool(player.getInventory().getItemInMainHand())) {
            ClaimTool claimTool = pl3xPlayer.getClaimTool();
            Claim claim = plugin.getClaimManager().getClaim(clickedBlock.getLocation());
            if (claim == null || (claimTool.getClaim() != null && claimTool.getClaim() != claim)) {
                // is creating claim
                if (claimTool.getPrimary() == null || claimTool.getClaim() != claim) {
                    pl3xPlayer.revertVisualization();

                    // this is first click
                    claimTool.setClaim(null);
                    claimTool.setPrimary(clickedBlock.getLocation());
                    claimTool.setSecondary(null);

                    // TODO visualize claim creation point
                    pl3xPlayer.showVisualization(new Claim(-99, null, null,
                                    new Coordinates(clickedBlock.getLocation(), clickedBlock.getLocation()), false),
                            VisualizationType.NEW_POINT);
                    return;
                }

                // this is second click
                claimTool.setSecondary(clickedBlock.getLocation());

                // build the new claim's coordinates
                Coordinates coordinates = new Coordinates(claimTool.getPrimary(), claimTool.getSecondary());

                // find the parent claim, if any, and verify this child is fully inside
                Claim parent = null;
                if (claimTool instanceof ChildClaimTool) {
                    parent = ((ChildClaimTool) claimTool).getParent();
                    if (!parent.getCoordinates().contains(coordinates)) {
                        // this claim is not fully inside the parent claim
                        // TODO inform player
                        //
                        //

                        pl3xPlayer.showVisualization(parent, VisualizationType.ERROR);
                        return;
                    }
                }

                // make the new claim
                boolean isAdminClaim = claimTool instanceof AdminClaimTool;
                claim = new Claim(plugin.getClaimManager().getNextId(), player.getUniqueId(), parent, coordinates, isAdminClaim);

                // make sure not overlapping other claims
                if (parent == null) {
                    // check if overlapping other top level claims
                    for (Claim topLevelClaim : plugin.getClaimManager().getTopLevelClaims()) {
                        if (claim.getCoordinates().overlaps(topLevelClaim.getCoordinates())) {
                            // TODO inform player
                            //
                            //

                            pl3xPlayer.showVisualization(topLevelClaim, VisualizationType.ERROR);
                            return;
                        }
                    }
                } else {
                    // check if overlapping other child claims
                    for (Claim childClaim : parent.getChildren()) {
                        if (claim.getCoordinates().overlaps(childClaim.getCoordinates())) {
                            // TODO inform player
                            //
                            //

                            pl3xPlayer.showVisualization(childClaim, VisualizationType.ERROR);
                            return;
                        }
                    }
                }

                CreateClaimEvent createClaimEvent = new CreateClaimEvent(player, claim, isAdminClaim);
                Bukkit.getPluginManager().callEvent(createClaimEvent);
                if (createClaimEvent.isCancelled()) {
                    return; // cancelled by plugin
                }

                // register/store the new claim
                plugin.getClaimManager().createNewClaim(claim);

                claimTool.setClaim(null);
                claimTool.setPrimary(null);
                claimTool.setSecondary(null);

                // TODO tell player about the new claim

                pl3xPlayer.showVisualization(claim);
                return;
            }

            // is resizing claim
            if (claimTool.getPrimary() == null) {
                // this is first click
                claimTool.setClaim(claim);

                if (claim.getCoordinates().isCorner(clickedBlock.getLocation())) {
                    // Clicked a corner
                    claimTool.setPrimary(clickedBlock.getLocation());
                    Lang.send(player, Lang.RESIZE_START);

                    pl3xPlayer.showVisualization(claim);
                    return;
                } else {

                }

                //
            } else {
                // this is second click

                if (claimTool.getClaim() != claim) {
                    // this is inside a different claim! error

                    // TODO notify player

                    claimTool.setClaim(null);
                    claimTool.setPrimary(null);
                    claimTool.setSecondary(null);
                    pl3xPlayer.showVisualization(claim, VisualizationType.ERROR);
                    return;
                }

                // check if claim overlaps other claims (child and top level)
                //
                //

                // resize the claim
                //
                //

                // TODO tell player about the resized claim
            }

            pl3xPlayer.showVisualization(claim);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (!Config.isWorldEnabled(player.getWorld())) {
            return; // claims not enabled in this world
        }

        if (!Config.isClaimTool(player.getInventory().getItem(event.getNewSlot()))) {
            return; // not switching to claim tool
        }

        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player);
        pl3xPlayer.revertVisualization();

        ClaimTool claimTool = new BasicClaimTool();
        pl3xPlayer.setClaimTool(claimTool);

        Claim claim = pl3xPlayer.inClaim();
        if (claim == null || !claim.isOwner(player)) {
            return; // no claim here or not owner of this claim
        }

        claimTool.setClaim(claim);
        claimTool.setPrimary(claim.getCoordinates().getMinLocation());
        claimTool.setSecondary(claim.getCoordinates().getMaxLocation());

        pl3xPlayer.showVisualization(claim);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        // ensure a new claim tool when changing worlds
        plugin.getPlayerManager().getPlayer(event.getPlayer()).setClaimTool(new BasicClaimTool());
    }
}
