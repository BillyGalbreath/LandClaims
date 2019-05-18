package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.flag.FlagType;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.util.EntityUtil;
import net.pl3x.bukkit.claims.util.Tags;
import org.bukkit.Location;
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
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FlagListener implements Listener {
    private final LandClaims plugin;

    public FlagListener(LandClaims plugin) {
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

        // dont let blocks explode inside this claim
        event.blockList().removeAll(event.blockList().stream()
                .filter(block -> plugin.getClaimManager().getClaim(block.getLocation()) != null)
                .collect(Collectors.toSet()));
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
        if (block.getType() != Material.FARMLAND && !Tags.FARMABLE.isTagged(block)) {
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

    /*
     * Mob Griefing hanging (item frame, paintings, etc)
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityBreakHangingEntity(HangingBreakByEntityEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getEntity().getLocation());
        if (claim == null) {
            return; // not in claim
        }

        if (claim.getFlag(FlagType.MOB_GRIEFING)) {
            return; // allow mob griefing
        }

        Entity remover = event.getRemover();
        if (!(remover instanceof Player || remover instanceof Projectile)) {
            event.setCancelled(true); // cancel mob griefing
        } else if (remover instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) remover).getShooter();
            if (!(shooter instanceof Player)) {
                event.setCancelled(true); // cancel mob griefing
            }
        }
    }

    /*
     * PvP & Mob Damage
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        if (event.getAffectedEntities().stream()
                .anyMatch(entity -> cancelDamage(event.getEntity(), entity))) {
            event.setCancelled(true);
        }
    }

    /*
     * PvP & Mob Damage
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        if (Config.isWorldDisabled(event.getAreaEffectCloud().getWorld())) {
            return; // claims not enabled in this world
        }

        switch (event.getAreaEffectCloud().getBasePotionData().getType()) {
            case SLOWNESS:
            case INSTANT_DAMAGE:
            case POISON:
            case WEAKNESS:
                break;
            default:
                return; // we only care about bad potion effects
        }

        Location loc = event.getAreaEffectCloud().getLocation();
        float radius = event.getAreaEffectCloud().getRadius();
        float radiusSquared = radius * radius;
        int y = loc.getBlockY();
        Set<Block> effectedBlocks = new HashSet<>();
        for (float x = -radius; x <= radius; x++) {
            for (float z = -radius; z <= radius; z++) {
                if ((x * x) + (z * z) <= radiusSquared) {
                    effectedBlocks.add(loc.getWorld().getBlockAt((int) x, y, (int) z));
                }
            }
        }

        if (effectedBlocks.isEmpty()) {
            return; // no effected area ???
        }

        ProjectileSource shooter = event.getEntity().getShooter();
        FlagType flag = shooter instanceof Player ? FlagType.PVP : FlagType.MOB_DAMAGE;

        if (effectedBlocks.stream()
                .map(block -> plugin.getClaimManager().getClaim(block.getLocation()))
                .anyMatch(claim -> claim != null && !claim.getFlag(flag))) {
            event.setCancelled(true);
            if (flag == FlagType.PVP) {
                Lang.send((Player) shooter, Lang.PVP_DENY);
            }
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockX() == event.getFrom().getBlockX()
                && event.getTo().getBlockY() == event.getFrom().getBlockY()
                && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
            return; // did not move a full block
        }

        Claim from = plugin.getClaimManager().getClaim(event.getFrom());
        Claim to = plugin.getClaimManager().getClaim(event.getTo());

        if (from == to) {
            return;
        }

        Player player = event.getPlayer();

        if (from != null && !from.getFlag(FlagType.EXIT) && !from.allowAccess(player)) {
            event.setTo(event.getFrom()); // cancel movement
            Lang.send(player, Lang.EXIT_DENIED);
        }

        if (to != null && !to.getFlag(FlagType.ENTRY) && !to.allowAccess(player)) {
            event.setTo(event.getFrom()); // cancel movement
            Lang.send(player, Lang.ENTRY_DENIED);
        }
    }
}
