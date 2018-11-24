package net.pl3x.bukkit.claims.visualization;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.visualization.task.VisualizationApplyTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class Visualization {
    private final Pl3xClaims plugin;
    private final Collection<VisualizationElement> elements = new HashSet<>();
    private final Location center;

    public Visualization(Pl3xClaims plugin, Collection<Claim> claims, VisualizationType type, Location center) {
        this.plugin = plugin;
        this.center = center;
        claims.forEach(claim -> addClaimElements(claim, type));
    }

    public Collection<VisualizationElement> getElements() {
        return elements;
    }

    public Location getCenter() {
        return center;
    }

    public void apply(Pl3xClaims plugin, Player player) {
        if (!player.isOnline()) {
            return; // sanity check
        }

        plugin.getPlayerManager().getPlayer(player).revertVisualization(); // revert any old visualization first

        if (getElements().isEmpty()) {
            return; // nothing to show
        }

        if (!player.getWorld().equals(getCenter().getWorld())) {
            return; // not in same world
        }

        new VisualizationApplyTask(plugin, player, this)
                .runTaskLater(plugin, 1L);
    }

    public void revert(Player player) {
        if (!player.isOnline()) {
            return; // sanity check
        }

        Visualization visualization = plugin.getPlayerManager().getPlayer(player).getVisualization();
        if (visualization == null) {
            return; // nothing to revert
        }

        if (!player.getWorld().equals(visualization.getCenter().getWorld())) {
            return; // not in same world
        }

        //remove any elements which are too far away (+150 blocks away)
        removeOutOfRange(visualization.getElements(),
                player.getLocation().getBlockX() - 100,
                player.getLocation().getBlockZ() - 100,
                player.getLocation().getBlockX() + 100,
                player.getLocation().getBlockZ() + 100);

        visualization.getElements().removeIf(e ->
                e.getLocation().distanceSquared(player.getLocation()) > 22500);

        //send real block information for any remaining elements
        for (VisualizationElement element : visualization.getElements()) {
            Block block = element.getLocation().getBlock();
            player.sendBlockChange(element.getLocation(), block.getType(), block.getData());
        }

        plugin.getPlayerManager().getPlayer(player).setVisualization(null);
    }

    public void addClaimElements(Claim claim, VisualizationType visualizationType) {
        boolean waterIsTransparent = center.getBlock().getType() == Material.WATER;

        World world = claim.getCoordinates().getWorld();
        int minX = (int) claim.getCoordinates().getMinLocation().getX();
        int minZ = (int) claim.getCoordinates().getMinLocation().getZ();
        int maxX = (int) claim.getCoordinates().getMaxLocation().getX();
        int maxZ = (int) claim.getCoordinates().getMaxLocation().getZ();

        Material cornerMaterial;
        Material accentMaterial;

        if (visualizationType == VisualizationType.CLAIM && claim.isAdminClaim()) {
            visualizationType = VisualizationType.ADMIN;
        }

        if (visualizationType == VisualizationType.CLAIM && claim.getParent() != null) {
            addClaimElements(claim.getParent(), VisualizationType.CLAIM);
            claim.getParent().getChildren().forEach(child ->
                    addClaimElements(child, VisualizationType.CHILD));
            return;
        }

        Collection<VisualizationElement> newElements = new HashSet<>();

        if (visualizationType == VisualizationType.CLAIM) {
            cornerMaterial = Material.GLOWSTONE;
            accentMaterial = Material.GOLD_BLOCK;
        } else if (visualizationType == VisualizationType.ADMIN) {
            cornerMaterial = Material.GLOWSTONE;
            accentMaterial = Material.PUMPKIN;
        } else if (visualizationType == VisualizationType.CHILD) {
            cornerMaterial = Material.IRON_BLOCK;
            accentMaterial = Material.WHITE_WOOL;
        } else if (visualizationType == VisualizationType.NEW_POINT) {
            cornerMaterial = Material.DIAMOND_BLOCK;
            accentMaterial = Material.DIAMOND_BLOCK;
        } else {
            cornerMaterial = Material.REDSTONE_ORE;
            accentMaterial = Material.NETHERRACK;
        }

        int centerMinX = center.getBlockX() - 75;
        int centerMinZ = center.getBlockZ() - 75;
        int centerMaxX = center.getBlockX() + 75;
        int centerMaxZ = center.getBlockZ() + 75;

        final int STEP = 10;

        //top line
        newElements.add(new VisualizationElement(new Location(world, minX, 0, maxZ), cornerMaterial, (byte) 0));
        newElements.add(new VisualizationElement(new Location(world, minX + 1, 0, maxZ), accentMaterial, (byte) 0));
        for (int x = minX + STEP; x < maxX - STEP / 2; x += STEP) {
            if (x > centerMinX && x < centerMaxX) {
                newElements.add(new VisualizationElement(new Location(world, x, 0, maxZ), accentMaterial, (byte) 0));
            }
        }
        newElements.add(new VisualizationElement(new Location(world, maxX - 1, 0, maxZ), accentMaterial, (byte) 0));

        //bottom line
        newElements.add(new VisualizationElement(new Location(world, minX + 1, 0, minZ), accentMaterial, (byte) 0));
        for (int x = minX + STEP; x < maxX - STEP / 2; x += STEP) {
            if (x > centerMinX && x < centerMaxX) {
                newElements.add(new VisualizationElement(new Location(world, x, 0, minZ), accentMaterial, (byte) 0));
            }
        }
        newElements.add(new VisualizationElement(new Location(world, maxX - 1, 0, minZ), accentMaterial, (byte) 0));

        //left line
        newElements.add(new VisualizationElement(new Location(world, minX, 0, minZ), cornerMaterial, (byte) 0));
        newElements.add(new VisualizationElement(new Location(world, minX, 0, minZ + 1), accentMaterial, (byte) 0));
        for (int z = minZ + STEP; z < maxZ - STEP / 2; z += STEP) {
            if (z > centerMinZ && z < centerMaxZ) {
                newElements.add(new VisualizationElement(new Location(world, minX, 0, z), accentMaterial, (byte) 0));
            }
        }
        newElements.add(new VisualizationElement(new Location(world, minX, 0, maxZ - 1), accentMaterial, (byte) 0));

        //right line
        newElements.add(new VisualizationElement(new Location(world, maxX, 0, minZ), cornerMaterial, (byte) 0));
        newElements.add(new VisualizationElement(new Location(world, maxX, 0, minZ + 1), accentMaterial, (byte) 0));
        for (int z = minZ + STEP; z < maxZ - STEP / 2; z += STEP) {
            if (z > centerMinZ && z < centerMaxZ) {
                newElements.add(new VisualizationElement(new Location(world, maxX, 0, z), accentMaterial, (byte) 0));
            }
        }
        newElements.add(new VisualizationElement(new Location(world, maxX, 0, maxZ - 1), accentMaterial, (byte) 0));
        newElements.add(new VisualizationElement(new Location(world, maxX, 0, maxZ), cornerMaterial, (byte) 0));

        //remove any out of range elements
        removeOutOfRange(newElements, centerMinX, centerMinZ, centerMaxX, centerMaxZ);

        //remove any elements outside the claim
        newElements.removeIf(e ->
                !claim.getCoordinates().contains(e.getLocation()));

        //set Y values and real block information for any remaining visualization blocks
        int height = center.getBlockY();
        for (VisualizationElement element : newElements) {
            element.setLocation(getVisibleLocation(element.getLocation().getWorld(),
                    element.getLocation().getBlockX(), height, element.getLocation().getBlockZ(), waterIsTransparent));
            height = element.getLocation().getBlockY();
        }

        this.elements.addAll(newElements);
    }

    private Location getVisibleLocation(World world, int x, int y, int z, boolean waterIsTransparent) {
        Block block = world.getBlockAt(x, y, z);
        BlockFace direction = isTransparent(block, waterIsTransparent) ? BlockFace.DOWN : BlockFace.UP;
        while (block.getY() >= 1 && block.getY() < world.getMaxHeight() - 1 &&
                (!isTransparent(block.getRelative(BlockFace.UP), waterIsTransparent) || isTransparent(block, waterIsTransparent))) {
            block = block.getRelative(direction);
        }
        return block.getLocation();
    }

    private boolean isTransparent(Block block, boolean waterIsTransparent) {
        switch (block.getType()) {
            case SNOW:
                return false;
        }
        switch (block.getType()) {
            case OAK_FENCE:
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case NETHER_BRICK_FENCE:
            case SPRUCE_FENCE:
            case OAK_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SIGN:
            case WALL_SIGN:
                return true;
        }
        return (waterIsTransparent && block.getType() == Material.WATER) || block.getType().isTransparent();
    }

    private void removeOutOfRange(Collection<VisualizationElement> elements, int minX, int minZ, int maxX, int maxZ) {
        elements.removeIf(element -> {
            Location location = element.getLocation();
            return location.getX() < minX || location.getX() > maxX || location.getZ() < minZ || location.getZ() > maxZ;
        });
    }
}
