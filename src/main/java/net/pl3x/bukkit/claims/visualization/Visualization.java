package net.pl3x.bukkit.claims.visualization;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class Visualization {
    private final Collection<VisualizationElement> elements = new HashSet<>();
    private final World world;

    public Visualization(World world) {
        this.world = world;
    }

    public Collection<VisualizationElement> getElements() {
        return elements;
    }

    public static void apply(Player player, Visualization visualization) {
        if (!player.isOnline()) {
            return; // sanity check
        }

        if (Pl3xPlayer.getPlayer(player).getVisualization() != null) {
            Visualization.revert(player);
        }

        if (visualization == null || visualization.getElements().isEmpty()) {
            return; // nothing to show
        }

        if (!player.getWorld().equals(visualization.world)) {
            return; // not in same world
        }

        new VisualizationApplyTask(player, visualization).runTaskLater(Pl3xClaims.getPlugin(), 1L);
    }

    public static void revert(Player player) {
        if (!player.isOnline()) {
            return; // sanity check
        }

        Visualization visualization = Pl3xPlayer.getPlayer(player).getVisualization();
        if (visualization == null) {
            return; // nothing to revert
        }

        if (!player.getWorld().equals(visualization.world)) {
            return; // not in same world
        }

        //remove any elements which are too far away (+150 blocks away)
        visualization.getElements().removeIf(e ->
                e.getLocation().distanceSquared(player.getLocation()) > 22500);

        //send real block information for any remaining elements
        for (VisualizationElement element : visualization.getElements()) {
            Block block = element.getLocation().getBlock();
            //noinspection deprecation
            player.sendBlockChange(element.getLocation(), block.getType(), block.getData());
        }

        Pl3xPlayer.getPlayer(player).setVisualization(null);
    }

    public static Visualization fromClaims(Collection<Claim> claims, int height, VisualizationType visualizationType, Location center) {
        Visualization visualization = new Visualization(center.getWorld());
        claims.forEach(claim -> visualization.addClaimElements(claim, height, visualizationType, center));
        return visualization;
    }

    private void addClaimElements(Claim claim, int height, VisualizationType visualizationType, Location center) {
        boolean waterIsTransparent = center.getBlock().getType() == Material.STATIONARY_WATER;

        World world = claim.getCoordinates().getWorld();
        int minX = (int) claim.getCoordinates().getMinLocation().getX();
        int minZ = (int) claim.getCoordinates().getMinLocation().getZ();
        int maxX = (int) claim.getCoordinates().getMaxLocation().getX();
        int maxZ = (int) claim.getCoordinates().getMaxLocation().getZ();

        Material cornerMaterial;
        Material accentMaterial;

        Collection<VisualizationElement> newElements = new HashSet<>();

        if (visualizationType == VisualizationType.CLAIM) {
            cornerMaterial = Material.GLOWSTONE;
            accentMaterial = Material.GOLD_BLOCK;
        } else if (visualizationType == VisualizationType.CHILD) {
            cornerMaterial = Material.IRON_BLOCK;
            accentMaterial = Material.WOOL;
        } else if (visualizationType == VisualizationType.ADMIN) {
            cornerMaterial = Material.GLOWSTONE;
            accentMaterial = Material.PUMPKIN;
        } else {
            cornerMaterial = Material.GLOWING_REDSTONE_ORE;
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
        newElements.removeIf(e ->
                e.getLocation().distanceSquared(center) > 22500);

        //remove any elements outside the claim
        newElements.removeIf(e ->
                !claim.getCoordinates().contains(e.getLocation()));

        //set Y values and real block information for any remaining visualization blocks
        for (VisualizationElement element : newElements) {
            element.setLocation(getVisibleLocation(element.getLocation().getWorld(), element.getLocation().getBlockX(), height, element.getLocation().getBlockZ(), waterIsTransparent));
            height = element.getLocation().getBlockY();
        }

        this.elements.addAll(newElements);
    }

    private static Location getVisibleLocation(World world, int x, int y, int z, boolean waterIsTransparent) {
        Block block = world.getBlockAt(x, y, z);
        BlockFace direction = (isTransparent(block, waterIsTransparent)) ? BlockFace.DOWN : BlockFace.UP;
        while (block.getY() >= 1 && block.getY() < world.getMaxHeight() - 1 &&
                (!isTransparent(block.getRelative(BlockFace.UP), waterIsTransparent) || isTransparent(block, waterIsTransparent))) {
            block = block.getRelative(direction);
        }
        return block.getLocation();
    }

    private static boolean isTransparent(Block block, boolean waterIsTransparent) {
        switch (block.getType()) {
            case SNOW:
                return false;
        }

        switch (block.getType()) {
            case FENCE:
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
            case FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SIGN:
            case SIGN_POST:
            case WALL_SIGN:
                return true;
        }

        return (waterIsTransparent && block.getType() == Material.STATIONARY_WATER) || block.getType().isTransparent();
    }
}
