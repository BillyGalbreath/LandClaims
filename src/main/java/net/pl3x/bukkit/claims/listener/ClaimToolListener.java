package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.ClaimManager;
import net.pl3x.bukkit.claims.claim.Coordinates;
import net.pl3x.bukkit.claims.claim.tool.AdminClaimTool;
import net.pl3x.bukkit.claims.claim.tool.BasicClaimTool;
import net.pl3x.bukkit.claims.claim.tool.ChildClaimTool;
import net.pl3x.bukkit.claims.claim.tool.ClaimTool;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.event.CreateClaimEvent;
import net.pl3x.bukkit.claims.event.InspectClaimsEvent;
import net.pl3x.bukkit.claims.event.VisualizeClaimsEvent;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // only care about main hand packet
        }

        Player player = event.getPlayer();
        if (Config.isWorldEnabled(player.getWorld())) {
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

        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            if (Config.isInspectTool(player.getInventory().getItemInMainHand())) {
                Lang.send(player, Lang.INSPECT_TOO_FAR);
            }
            return; // no block clicked
        }

        if (Config.isInspectTool(player.getInventory().getItemInMainHand())) {
            if (!player.hasPermission("claims.visualize")) {
                return;
            }
            if (player.isSneaking() && player.hasPermission("claims.visualize.nearby")) {
                // is inspecting all nearby claims
                Collection<Claim> nearbyClaims = ClaimManager.getInstance().getNearbyClaims(player.getLocation());

                InspectClaimsEvent inspectClaimsEvent = new InspectClaimsEvent(player, nearbyClaims);
                Bukkit.getPluginManager().callEvent(inspectClaimsEvent);
                if (inspectClaimsEvent.isCancelled()) {
                    return; // cancelled by plugin
                }

                if (nearbyClaims == null || nearbyClaims.isEmpty()) {
                    Lang.send(player, Lang.INSPECT_NO_CLAIM);
                    return;
                }

                // TODO tell player how many claims
                //
                //

                VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player, nearbyClaims);
                Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
                if (visualizeClaimsEvent.isCancelled()) {
                    return; // cancelled by plugin
                }

                // TODO visualize claims
                //
                //
                return;
            }

            // is inspecting a single claim
            Claim claim = ClaimManager.getInstance().getClaim(clickedBlock.getLocation());

            InspectClaimsEvent inspectClaimsEvent = new InspectClaimsEvent(player, claim);
            Bukkit.getPluginManager().callEvent(inspectClaimsEvent);
            if (inspectClaimsEvent.isCancelled()) {
                return; // cancelled by plugin
            }

            if (claim == null) {
                Lang.send(player, Lang.INSPECT_NO_CLAIM);
                return;
            }

            // TODO tell player about the claim
            //
            //

            VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player, claim);
            Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
            if (visualizeClaimsEvent.isCancelled()) {
                return; // cancelled by plugin
            }

            // TODO visualize claim
            //
            //
            return;
        }

        if (Config.isClaimTool(player.getInventory().getItemInMainHand())) {
            Pl3xPlayer pl3xPlayer = Pl3xPlayer.getPlayer(player);
            ClaimTool claimTool = pl3xPlayer.getClaimTool();
            Claim claim = ClaimManager.getInstance().getClaim(clickedBlock.getLocation());
            if (claim == null) {
                // is creating claim
                if (claimTool.getPrimary() == null) {
                    // this is first click
                    claimTool.setPrimary(clickedBlock.getLocation());

                    // TODO visualize claim creation point
                    //
                    //
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

                        VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player, parent);
                        Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
                        if (visualizeClaimsEvent.isCancelled()) {
                            return; // cancelled by plugin
                        }

                        // TODO visualize parent claim
                        //
                        //
                        return;
                    }
                }

                // make the new claim
                boolean isAdminClaim = claimTool instanceof AdminClaimTool;
                claim = new Claim(ClaimManager.getInstance().getNextId(), player.getUniqueId(), parent, coordinates);

                CreateClaimEvent createClaimEvent = new CreateClaimEvent(player, claim, isAdminClaim);
                Bukkit.getPluginManager().callEvent(createClaimEvent);
                if (createClaimEvent.isCancelled()) {
                    return; // cancelled by plugin
                }

                // register/store the new claim
                ClaimManager.getInstance().createNewClaim(claim);

                VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player, claim);
                Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
                if (visualizeClaimsEvent.isCancelled()) {
                    return; // cancelled by plugin
                }

                // TODO visualize the new claim
                //
                //
                return;
            }

            // is resizing claim
            if (claimTool.getPrimary() == null) {
                // this is first click
                claimTool.setPrimary(clickedBlock.getLocation());
            } else {
                // this is second click

                // check if claim overlaps other claims (child and top level)
                //
                //

                // resize the claim
                //
                //
            }

            VisualizeClaimsEvent visualizeClaimsEvent = new VisualizeClaimsEvent(player, claim);
            Bukkit.getPluginManager().callEvent(visualizeClaimsEvent);
            if (visualizeClaimsEvent.isCancelled()) {
                return; // cancelled by plugin
            }

            // TODO visualize claim
            //
            //
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (Config.isWorldEnabled(player.getWorld())) {
            return; // claims not enabled in this world
        }

        Pl3xPlayer pl3xPlayer = Pl3xPlayer.getPlayer(player);
        Claim claim = pl3xPlayer.inClaim();
        if (claim == null) {
            return; // not in a claim
        }

        if (claim.getOwner() != player.getUniqueId()) {
            return; // not owner of this claim
        }

        if (!Config.isClaimTool(player.getInventory().getItem(event.getNewSlot()))) {
            return; // not switching to claim tool
        }

        ClaimTool claimTool = new BasicClaimTool();
        pl3xPlayer.setClaimTool(claimTool);

        claimTool.setClaim(claim);
        claimTool.setPrimary(claim.getCoordinates().getMinLocation());
        claimTool.setSecondary(claim.getCoordinates().getMaxLocation());

        // TODO visualize claim
        //
        //
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        // ensure a new claim tool when changing worlds
        Pl3xPlayer.getPlayer(event.getPlayer()).setClaimTool(new BasicClaimTool());
    }
}
