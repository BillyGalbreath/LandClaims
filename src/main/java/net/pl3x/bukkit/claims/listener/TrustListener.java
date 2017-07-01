package net.pl3x.bukkit.claims.listener;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.util.BlockUtil;
import net.pl3x.bukkit.claims.util.EntityUtil;
import net.pl3x.bukkit.claims.util.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class TrustListener implements Listener {
    private final Pl3xClaims plugin;

    public TrustListener(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    /*
     * Stops players from breaking blocks
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }
        if (plugin.getPlayerManager().getPlayer(event.getPlayer()).isIgnoringClaims()) {
            return; // overrides claims
        }
        Claim claim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (claim == null) {
            return;
        }

        // container trust
        if (event.getBlock().getType() == Material.MELON_BLOCK ||
                event.getBlock().getType() == Material.PUMPKIN ||
                event.getBlock().getType() == Material.CROPS ||
                event.getBlock().getType() == Material.BEETROOT_BLOCK ||
                event.getBlock().getType() == Material.POTATO ||
                event.getBlock().getType() == Material.CARROT ||
                event.getBlock().getType() == Material.COCOA ||
                event.getBlock().getType() == Material.SUGAR_CANE_BLOCK ||
                event.getBlock().getType() == Material.NETHER_WART_BLOCK ||
                event.getBlock().getType() == Material.CACTUS) {
            if (!claim.allowContainers(event.getPlayer())) {
                Lang.send(event.getPlayer(), Lang.CONTAINER_DENY);
                event.setCancelled(true);
            }
        }

        // build trust
        else if (!claim.allowBuild(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.BUILD_DENY);
            event.setCancelled(true);
        }
    }

    /*
     * Stops players from placing blocks
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (plugin.getPlayerManager().getPlayer(event.getPlayer()).isIgnoringClaims()) {
            return; // overrides claims
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (claim == null) {
            return;
        }

        // container trust
        if (event.getBlock().getType() == Material.MELON_BLOCK ||
                event.getBlock().getType() == Material.PUMPKIN ||
                event.getBlock().getType() == Material.CROPS ||
                event.getBlock().getType() == Material.BEETROOT_BLOCK ||
                event.getBlock().getType() == Material.POTATO ||
                event.getBlock().getType() == Material.CARROT ||
                event.getBlock().getType() == Material.COCOA ||
                event.getBlock().getType() == Material.SUGAR_CANE_BLOCK ||
                event.getBlock().getType() == Material.NETHER_WART_BLOCK ||
                event.getBlock().getType() == Material.CACTUS) {
            if (!claim.allowContainers(event.getPlayer())) {
                Lang.send(event.getPlayer(), Lang.CONTAINER_DENY);
                event.setCancelled(true);
            }
        }

        // build trust
        else if (!claim.allowBuild(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.BUILD_DENY);
            event.setCancelled(true);
        }
    }

    /*
     * Stops players from placing multiple blocks
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlocksPlace(BlockMultiPlaceEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (plugin.getPlayerManager().getPlayer(event.getPlayer()).isIgnoringClaims()) {
            return; // overrides claims
        }

        for (BlockState state : event.getReplacedBlockStates()) {
            Claim claim = plugin.getClaimManager().getClaim(state.getLocation());
            if (claim != null && !claim.allowBuild(event.getPlayer())) {
                Lang.send(event.getPlayer(), Lang.BUILD_DENY);
                event.setCancelled(true);
                return;
            }
        }
    }

    /*
     * Stops players from forming blocks (frost walker, etc)
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerFormBlock(EntityBlockFormEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        if (plugin.getPlayerManager().getPlayer((Player) event.getEntity()).isIgnoringClaims()) {
            return; // overrides claims
        }

        Player player = (Player) event.getEntity();
        Claim claim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (claim != null && !claim.allowBuild(player)) {
            // do not notify player (to prevent spam)
            event.setCancelled(true);
        }
    }

    /*
     * Stops players from editing signs without build rights using plugins/mods
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // claims not enabled in this world
        }

        if (plugin.getPlayerManager().getPlayer(event.getPlayer()).isIgnoringClaims()) {
            return; // overrides claims
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getBlock().getLocation());
        if (claim == null) {
            return;
        }

        if (!claim.allowBuild(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.BUILD_DENY);
            event.setCancelled(true);
        }
    }

    /*
     * Stops players from hurting non-mob entities (animals, armorstands, etc)
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerHurtEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (Config.isWorldDisabled(entity.getWorld())) {
            return;
        }

        if (EntityUtil.isMob(entity)) {
            return; // dont care about hurting mobs
        }

        Entity killer = event.getDamager();
        Player player = null;
        if (killer instanceof Player) {
            player = (Player) killer;
        } else if (killer instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) killer).getShooter();
            if (shooter instanceof Player) {
                player = (Player) shooter;
            }
        }

        if (player == null) {
            return; // not killed by player
        }

        if (plugin.getPlayerManager().getPlayer(player).isIgnoringClaims()) {
            return; // overrides claims
        }

        Claim claim = plugin.getClaimManager().getClaim(entity.getLocation());
        if (claim == null) {
            return;
        }

        if (EntityUtil.isAnimal(entity)) {
            if (!claim.allowContainers(player)) {
                Lang.send(player, Lang.CONTAINER_DENY);
                event.setCancelled(true);
            }
        } else {
            if (!claim.allowBuild(player)) {
                Lang.send(player, Lang.BUILD_DENY);
                event.setCancelled(true);
            }
        }
    }

    /*
     * Stops reeling of animals, villagers, and armorstands
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        Entity entity = event.getCaught();
        if (entity == null) {
            return; // nothing reeled
        }

        if (Config.isWorldDisabled(entity.getWorld())) {
            return;
        }

        if (plugin.getPlayerManager().getPlayer(event.getPlayer()).isIgnoringClaims()) {
            return; // overrides claims
        }

        Claim claim = plugin.getClaimManager().getClaim(entity.getLocation());
        if (claim == null) {
            return;
        }

        if ((entity.getType() == EntityType.ARMOR_STAND ||
                entity.getType() == EntityType.VILLAGER ||
                entity instanceof Animals) &&
                !claim.allowContainers(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.CONTAINER_DENY);
            event.setCancelled(true);
        }
    }

    /*
     * Prevent players from harming protected animals with flame bow
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityCombustEntity(EntityCombustByEntityEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        if (!EntityUtil.isAnimal(event.getEntity())) {
            return; // not an animal
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getEntity().getLocation());
        if (claim == null) {
            return; // animal is not protected
        }

        if (event.getCombuster() instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) event.getCombuster()).getShooter();
            if (shooter instanceof Player) {
                Player player = (Player) shooter;
                if (plugin.getPlayerManager().getPlayer(player).isIgnoringClaims()) {
                    return; // player is ignoring claims
                }
                if (claim.allowContainers(player)) {
                    return; // player is allowed to harm this animal
                }
            }
        }

        // cancel everything else (players, mobs, dispensers, etc)
        event.setCancelled(true);
    }

    /*
     * Interacting with an armorstand
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        //treat it the same as interacting with an entity in general
        if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            onPlayerInteractEntity(event);
        }
    }

    /*
     * Stops players from breaking minecarts,
     * interacting with animals and villagers,
     * and from leashing entities
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (Config.isWorldDisabled(entity.getWorld())) {
            return; // claims not enabled in this world
        }

        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
        if (pl3xPlayer.isIgnoringClaims()) {
            return;
        }

        Claim claim = plugin.getClaimManager().getClaim(entity.getLocation());
        if (claim == null) {
            return;
        }

        ItemStack itemInHand = ItemUtil.getItemInHand(event.getPlayer(), event.getHand());

        // (build trust)
        // check interaction with armorstands and item frames/paintings (build trust)
        if ((entity.getType() == EntityType.ARMOR_STAND || entity instanceof Hanging) && !claim.allowBuild(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.BUILD_DENY);
            event.setCancelled(true);
            return;
        }

        // (container trust)
        // check animals, villagers, inventory holder vehicles, and leashing mobs
        if ((EntityUtil.isAnimal(entity) ||
                entity instanceof Villager ||
                (entity instanceof Vehicle && entity instanceof InventoryHolder) ||
                (entity instanceof Creature && itemInHand.getType() == Material.LEASH)) &&
                !claim.allowContainers(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.CONTAINER_DENY);
            event.setCancelled(true);
        }
    }

    /*
     * Stops players from opening containers, placing minecarts
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (Config.isWorldDisabled(player.getWorld())) {
            return; // claims not enabled in this world
        }

        if (plugin.getPlayerManager().getPlayer(player).isIgnoringClaims()) {
            return; // overrides claims
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            return;
        }

        Claim claim = plugin.getClaimManager().getClaim(clickedBlock.getLocation());
        if (claim == null) {
            return;
        }

        ItemStack itemInHand = ItemUtil.getItemInHand(event.getPlayer(), event.getHand());

        // (container trust)
        // special farmland check
        if (clickedBlock.getType() == Material.SOIL &&
                (itemInHand.getType() == Material.SEEDS ||
                        itemInHand.getType() == Material.BEETROOT_SEEDS ||
                        itemInHand.getType() == Material.MELON_SEEDS ||
                        itemInHand.getType() == Material.PUMPKIN_SEEDS ||
                        itemInHand.getType() == Material.POTATO ||
                        itemInHand.getType() == Material.CARROT ||
                        itemInHand.getType() == Material.COCOA ||
                        itemInHand.getType() == Material.SUGAR_CANE ||
                        itemInHand.getType() == Material.NETHER_WARTS ||
                        itemInHand.getType() == Material.CACTUS)) {
            if (!claim.allowContainers(event.getPlayer())) {
                Lang.send(player, Lang.CONTAINER_DENY);
                event.setCancelled(true);
            }
        }

        // (container trust)
        // prevent opening containers
        // prevent placing minecarts
        else if (BlockUtil.isContainer(clickedBlock) || ItemUtil.isMinecart(itemInHand)) {
            if (!claim.allowContainers(event.getPlayer())) {
                Lang.send(player, Lang.CONTAINER_DENY);
                event.setCancelled(true);
            }
        }

        // (access trust)
        // prevent stealing cake
        // prevent using beds, doors, buttons, and levers
        else if (clickedBlock.getType() == Material.CAKE_BLOCK ||
                clickedBlock.getType() == Material.BED_BLOCK ||
                BlockUtil.isDoor(clickedBlock) ||
                BlockUtil.isButton(clickedBlock)) {
            if (!claim.allowAccess(player)) {
                Lang.send(player, Lang.ACCESS_DENY);
                event.setCancelled(true);
            }
        }

        // (build trust)
        // prevent using note blocks, repeaters, comparators, daylight sensors, dragon eggs, flower pots, and end crystals
        // prevent placing ink sack (bone meal), end crystals, armorstands, item frames, boats, and minecarts
        // prevent spawning monsters using eggs or monster blocks
        else if (clickedBlock.getType() == Material.NOTE_BLOCK ||
                clickedBlock.getType() == Material.DIODE_BLOCK_ON ||
                clickedBlock.getType() == Material.DIODE_BLOCK_OFF ||
                clickedBlock.getType() == Material.REDSTONE_COMPARATOR_ON ||
                clickedBlock.getType() == Material.REDSTONE_COMPARATOR_OFF ||
                clickedBlock.getType() == Material.DAYLIGHT_DETECTOR ||
                clickedBlock.getType() == Material.DAYLIGHT_DETECTOR_INVERTED ||
                clickedBlock.getType() == Material.DRAGON_EGG ||
                clickedBlock.getType() == Material.FLOWER_POT ||
                clickedBlock.getType() == Material.END_CRYSTAL ||
                itemInHand.getType() == Material.INK_SACK ||
                itemInHand.getType() == Material.END_CRYSTAL ||
                itemInHand.getType() == Material.ARMOR_STAND ||
                itemInHand.getType() == Material.ITEM_FRAME ||
                itemInHand.getType() == Material.MONSTER_EGG ||
                itemInHand.getType() == Material.MONSTER_EGGS ||
                ItemUtil.isBoat(itemInHand) ||
                ItemUtil.isMinecart(itemInHand)) {
            if (!claim.allowBuild(player)) {
                Lang.send(player, Lang.BUILD_DENY);
                event.setCancelled(true);
            }
        }
    }

    /*
     * Stops players from teleporting places they cannot access using chorus fruit and enderpearls
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (Config.isWorldDisabled(event.getTo().getWorld())) {
            return; // claims not enabled in this world
        }

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT &&
                event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        if (plugin.getPlayerManager().getPlayer(event.getPlayer()).isIgnoringClaims()) {
            return; // overrides claims
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getTo());
        if (claim == null) {
            return;
        }

        if (!claim.allowAccess(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.ACCESS_DENY);
            event.setCancelled(true);
        }
    }

    /*
     * Stops players from creating nether portals where they cannot build
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getTo() == null || event.getTo().getWorld() == null) {
            return; // not going anywhere
        }

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }

        if (Config.isWorldDisabled(event.getTo().getWorld())) {
            return; // claims not enabled in this world
        }

        if (plugin.getPlayerManager().getPlayer(event.getPlayer()).isIgnoringClaims()) {
            return; // overrides claims
        }

        Location destination = event.getTo();
        if (event.useTravelAgent()) {
            TravelAgent agent = event.getPortalTravelAgent();
            if (!agent.getCanCreatePortal()) {
                return; // cant create portal; nothing to check
            }
            agent.setCanCreatePortal(false);
            destination = agent.findOrCreate(destination);
            agent.setCanCreatePortal(true);
        }

        if (destination.getBlock().getType() == Material.PORTAL) {
            return; // already a portal there
        }

        Claim claim = plugin.getClaimManager().getClaim(destination);
        if (claim != null && !claim.allowBuild(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.BUILD_DENY);
            event.setCancelled(true);
        }
    }

    /*
     * Stops players from placing liquids
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerEmptyBucket(PlayerBucketEmptyEvent event) {
        if (Config.isWorldDisabled(event.getBlockClicked().getWorld())) {
            return; // claims not enabled in this world
        }

        if (plugin.getPlayerManager().getPlayer(event.getPlayer()).isIgnoringClaims()) {
            return; // overrides claims
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
        if (claim != null && !claim.allowBuild(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.BUILD_DENY);
            event.setCancelled(true);
        }
    }

    /*
     * Stops players from stealing liquids
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerFillBucket(PlayerBucketFillEvent event) {
        if (Config.isWorldDisabled(event.getBlockClicked().getWorld())) {
            return; // claims not enabled in this world
        }

        if (event.getBlockClicked().getType() == Material.AIR) {
            return; // clicked cow for milk; let interact event handle
        }

        if (plugin.getPlayerManager().getPlayer(event.getPlayer()).isIgnoringClaims()) {
            return; // overrides claims
        }

        Claim claim = plugin.getClaimManager().getClaim(event.getBlockClicked().getLocation());
        if (claim != null && !claim.allowBuild(event.getPlayer())) {
            Lang.send(event.getPlayer(), Lang.BUILD_DENY);
            event.setCancelled(true);
        }
    }

    /*
     * Stops players from breaking paintings, item frames, and leash hitches
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerHangingBreak(HangingBreakByEntityEvent event) {
        if (Config.isWorldDisabled(event.getEntity().getWorld())) {
            return; // claims not enabled in this world
        }

        Entity remover = event.getRemover();
        Hanging hanging = event.getEntity();

        Player player = null;
        if (remover instanceof Player) {
            player = (Player) remover;
        } else if (remover instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) remover).getShooter();
            if (shooter instanceof Player) {
                player = (Player) shooter;
            }
        }

        if (player == null) {
            return; // not broke by player
        }

        if (plugin.getPlayerManager().getPlayer(player).isIgnoringClaims()) {
            return; // overrides claims
        }

        Claim claim = plugin.getClaimManager().getClaim(hanging.getLocation());
        if (claim != null && !claim.allowBuild(player)) {
            Lang.send(player, Lang.BUILD_DENY);
            event.setCancelled(true);
        }
    }
}
