package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Config;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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

        //noinspection deprecation
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
     * Stop entities from changing blocks
     * rabbits eating crops, endermen moving blocks, silverfish breaking blocks,
     * wither breaking blocks
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (claim == null) {
            return;
        }

        if (event.getEntityType() == EntityType.ENDERMAN ||
                event.getEntityType() == EntityType.SILVERFISH ||
                event.getEntityType() == EntityType.RABBIT ||
                event.getEntityType() == EntityType.WITHER) {
            event.setCancelled(true);
        }
    }
}
