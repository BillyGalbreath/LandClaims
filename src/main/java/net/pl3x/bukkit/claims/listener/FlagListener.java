package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.flag.FlagType;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.util.EntityUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.projectiles.ProjectileSource;

public class FlagListener implements Listener {
    private final Pl3xClaims plugin;

    public FlagListener(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    /*
     * Explosions
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplosion(EntityExplodeEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        // explosions denied in this claim
        Claim from = plugin.getClaimManager().getClaim(event.getEntity().getLocation());
        if (from != null && !from.getFlag(FlagType.EXPLOSIONS)) {
            event.blockList().clear();
        }
    }

    /*
     * Fire Spread
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFireSpread(BlockSpreadEvent event) {
        if (event.getSource().getType() != Material.FIRE) {
            return; // not fire that is spreading
        }

        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        // claim has firespread flag disabled
        Claim toClaim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (toClaim != null && !toClaim.getFlag(FlagType.FIRESPREAD)) {
            event.setCancelled(true);
        }
    }

    /*
     * Another check for Fire Spread
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        //don't track in worlds where claims are not enabled
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (event.getIgnitingBlock() == null) {
            return;
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getIgnitingBlock().getLocation());
        if (claim == null) {
            return;
        }

        if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL &&
                event.getCause() != BlockIgniteEvent.IgniteCause.LIGHTNING &&
                !claim.getFlag(FlagType.FIRESPREAD)) {
            event.setCancelled(true);
        }
    }

    /*
     * Spawn Animals & Spawn Mobs
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        LivingEntity entity = event.getEntity();
        if (entity instanceof Villager) {
            return; // aways allow villagers
        }

        Claim claim = plugin.getClaimManager().getClaim(entity.getLocation());
        if (claim == null) {
            return;
        }

        // animals
        if (EntityUtil.isAnimal(entity) && !claim.getFlag(FlagType.SPAWN_ANIMALS)) {
            event.setCancelled(true);
            return;
        }

        // mobs
        if (EntityUtil.isMob(entity) && !claim.getFlag(FlagType.SPAWN_MOBS)) {
            event.setCancelled(true);
        }
    }

    /*
     * Mob Griefing
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobGrief(EntityChangeBlockEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (event.getEntityType() == EntityType.FALLING_BLOCK ||
                event.getEntityType() == EntityType.PLAYER) {
            return;
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (claim != null && !claim.getFlag(FlagType.MOB_GRIEFING)) {
            event.setCancelled(true);
        }
    }

    /*
     * Mob Griefing
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobGrief(EntityInteractEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        Block block = event.getBlock();
        if (block.getType() != Material.SOIL && block.getType() != Material.CROPS) {
            return;
        }

        Claim claim = plugin.getClaimManager().getClaim(block.getLocation());
        if (claim != null && !claim.getFlag(FlagType.MOB_GRIEFING)) {
            event.setCancelled(true);
        }
    }

    /*
     * PvP & Mob Damage & Mob Griefing
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        if (cancelDamage(event.getDamager(), event.getEntity())) {
            event.setCancelled(true);
        }
    }

    /*
     * PvP & Mob Damage & Mob Griefing
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityCombustEntity(EntityCombustByEntityEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        if (cancelDamage(event.getCombuster(), event.getEntity())) {
            event.setCancelled(true);
        }
    }

    private boolean cancelDamage(Entity damager, Entity damaged) {
        Claim damagedClaim = plugin.getClaimManager().getClaim(damaged.getLocation());
        Claim damagerClaim = plugin.getClaimManager().getClaim(damager.getLocation());

        if (damagedClaim == null && damagerClaim == null) {
            return false; // return out early if not in any claims
        }

        boolean pvp = false; // player hurt player
        boolean mobdamage = false; // mob hurt player
        boolean mobgriefing = false; // mob hurt entity

        if (damaged instanceof Player) {
            if (damager instanceof Player) {
                pvp = true; // player hurt player
            } else if (damager instanceof LivingEntity) {
                mobdamage = true; // mob hurt player
            } else if (damager instanceof Projectile) {
                ProjectileSource shooter = ((Projectile) damager).getShooter();
                if (shooter instanceof Player) {
                    pvp = true; // player hurt player
                } else if (shooter instanceof LivingEntity) {
                    mobdamage = true; // mob hurt player
                }
            }
        } else {
            if (!(damager instanceof Player) && damager instanceof LivingEntity) {
                if (EntityUtil.isAnimal(damaged) || damaged instanceof Villager) {
                    mobgriefing = true; // mob hurt animal or villager
                } else if (!EntityUtil.isMob(damaged)) {
                    mobgriefing = true; // mob hurt tile entity
                }
            } else if (damager instanceof Projectile) {
                ProjectileSource shooter = ((Projectile) damager).getShooter();
                if (!(shooter instanceof Player) && shooter instanceof LivingEntity) {
                    if (EntityUtil.isAnimal(damaged) || damaged instanceof Villager) {
                        mobgriefing = true; // mob hurt animal or villager
                    } else if (!EntityUtil.isMob(damaged)) {
                        mobgriefing = true; // mob hurt tile entity
                    }
                }
            }
        }

        // check both player positions
        if (pvp && ((damagerClaim != null && !damagerClaim.getFlag(FlagType.PVP)) ||
                (damagedClaim != null && !damagedClaim.getFlag(FlagType.PVP)))) {
            Lang.send(damager, Lang.PVP_DENY);
            return true; // cancel pvp
        }

        // check position of player mob is hitting
        else if (mobdamage && damagedClaim != null && !damagedClaim.getFlag(FlagType.MOB_DAMAGE)) {
            return true; // cancel mob damage
        }

        // check position of entity mob is griefing
        else if (mobgriefing && damagedClaim != null && !damagedClaim.getFlag(FlagType.MOB_GRIEFING)) {
            return true; // cancel mob griefing
        }

        return false;
    }
}
