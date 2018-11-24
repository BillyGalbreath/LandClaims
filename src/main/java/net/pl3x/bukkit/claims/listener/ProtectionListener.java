package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.util.MaterialTags;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Dispenser;

import java.util.Objects;
import java.util.stream.Collectors;

public class ProtectionListener implements Listener {
    private final Pl3xClaims plugin;

    public ProtectionListener(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    /*
     * Stop liquids from passing claim borders
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLiquidSpread(BlockFromToEvent event) {
        if (event.getFace() == BlockFace.DOWN) {
            return; // let liquids go straight down
        }

        if (!event.getBlock().isLiquid()) {
            return; // not a liquid
        }

        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (!Objects.equals(plugin.getClaimManager().getClaim(event.getBlock().getLocation()),
                plugin.getClaimManager().getClaim(event.getToBlock().getLocation()))) {
            event.setCancelled(true);
        }
    }

    /*
     * Stops pistons from pushing blocks passed claim borders
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPistonPush(BlockPistonExtendEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        Block piston = event.getBlock();
        Claim from = plugin.getClaimManager().getClaim(piston.getLocation());
        for (Block block : event.getBlocks()) {
            if (!Objects.equals(from, plugin.getClaimManager().getClaim(block.getLocation()))) {
                event.setCancelled(true);
                piston.setType(Material.AIR);
                piston.getWorld().createExplosion(piston.getLocation(), 0F);
                return;
            }
        }
    }

    /*
     * Stops pistons from pulling blocks passed claim borders
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPistonPull(BlockPistonRetractEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        Block piston = event.getBlock();
        Claim from = plugin.getClaimManager().getClaim(piston.getLocation());
        for (Block block : event.getBlocks()) {
            if (!Objects.equals(from, plugin.getClaimManager().getClaim(block.getLocation()))) {
                event.setCancelled(true);
                piston.setType(Material.AIR);
                piston.getWorld().createExplosion(piston.getLocation(), 0F);
                return;
            }
        }
    }

    /*
     * Stops dispensers from pushing blocks passed claim borders
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (!Objects.equals(plugin.getClaimManager().getClaim(event.getBlock().getLocation()),
                plugin.getClaimManager().getClaim(event.getBlock()
                        .getRelative(new Dispenser(Material.DISPENSER, event.getBlock().getData()).getFacing())
                        .getLocation()))) {
            event.setCancelled(true);
        }
    }

    /*
     * Stops explosions from passing claim borders
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplosion(EntityExplodeEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        /* // lets allow creeper explosions in the wilderness
        if (event.getEntityType() == EntityType.CREEPER) {
            event.blockList().clear();
            return; // NEVER let creepers make potholes
        }
        */

        Claim from = plugin.getClaimManager().getClaim(event.getEntity().getLocation());

        // dont let blocks explode passed claim borders
        event.blockList().removeAll(event.blockList().stream()
                .filter(block -> !Objects.equals(from, plugin.getClaimManager().getClaim(block.getLocation())))
                .collect(Collectors.toSet()));
    }

    /*
     * Stops fire from spreading passed claim borders
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFireSpread(BlockSpreadEvent event) {
        if (!event.getSource().getType().equals(Material.FIRE)) {
            return; // not fire that is spreading
        }

        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        // stop fire spread if crossing claim borders
        if (!Objects.equals(plugin.getClaimManager().getClaim(event.getSource().getLocation()),
                plugin.getClaimManager().getClaim(event.getBlock().getLocation()))) {
            event.setCancelled(true);
        }
    }

    /*
     * Stops trees from growing passed claim borders
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTreeGrow(StructureGrowEvent event) {
        if (Config.isWorldDisabled(event.getWorld())) {
            return; // claims not enabled in this world
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getLocation());
        event.getBlocks().removeIf(state ->
                !Objects.equals(claim, plugin.getClaimManager().getClaim(state.getLocation())));
    }

    /*
     * Stop claimed blocks from being destroyed by fire
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFireDestroy(BlockBurnEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (claim != null) {
            event.setCancelled(true);
        }
    }

    /*
     * Stop entities from changing blocks in the world
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        EntityType type = event.getEntityType();

        if (type == EntityType.ARROW) {
            type = ((Entity) ((Arrow) event.getEntity()).getShooter()).getType();
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (claim == null) {
            return; // no claim here
        }

        // stop these entities only if in a claim
        if (type == EntityType.ENDERMAN ||       // endermen steal blocks
                type == EntityType.SILVERFISH || // silverfish break/replace stone
                type == EntityType.RABBIT ||     // rabbit eats crops
                type == EntityType.WITHER ||     // wither explodes blocks
                type == EntityType.ZOMBIE ||     // zombie breaks doors
                type == EntityType.PIG_ZOMBIE || // zombie breaks doors
                type == EntityType.HUSK ||       // zombie breaks doors
                type == EntityType.DROWNED ||    // zombie breaks doors
                type == EntityType.SPIDER ||     // spider shoots webs (ridables)
                type == EntityType.SHEEP ||      // sheep eat grass
                type == EntityType.VILLAGER) {   // villagers farm crops
            event.setCancelled(true); // dont let mobs grief claims
        }
    }

    /*
     * Stops soil from being trampled by entities
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSoilTrample(EntityInteractEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (event.getBlock().getType() != Material.FARMLAND && !MaterialTags.FARMABLE.isTagged(event.getBlock())) {
            return; // not soil/crops
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (claim == null) {
            return;
        }

        event.setCancelled(true);
    }

    /*
     * Stops soil from being trampled by players
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSoilTrample(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        if (Config.isWorldDisabled(event.getClickedBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (event.getClickedBlock().getType() != Material.FARMLAND && !MaterialTags.FARMABLE.isTagged(event.getClickedBlock())) {
            return; // not soil/crops
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getClickedBlock().getLocation());
        if (claim == null) {
            return;
        }

        event.setCancelled(true);
    }

    /*
     * Stops dragon eggs from teleporting from/to claims
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDragonEggTouch(BlockFromToEvent event) {
        if (event.getBlock().getType() != Material.DRAGON_EGG) {
            return; // not a dragon egg
        }

        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        Claim fromClaim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        Claim toClaim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());

        if (fromClaim != null || toClaim != null) {
            event.setCancelled(true); // do not teleport the egg
        }
    }
}
