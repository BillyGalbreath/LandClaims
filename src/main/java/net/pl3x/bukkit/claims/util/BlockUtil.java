package net.pl3x.bukkit.claims.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;

public class BlockUtil {
    public static boolean isContainer(Block block) {
        return block.getState() instanceof InventoryHolder ||
                block.getType() == Material.CAULDRON ||
                block.getType() == Material.JUKEBOX ||
                block.getType() == Material.ANVIL ||
                block.getType() == Material.CAKE_BLOCK;
    }

    public static boolean isDoor(Block block) {
        return block.getType() == Material.WOODEN_DOOR ||
                block.getType() == Material.ACACIA_DOOR ||
                block.getType() == Material.BIRCH_DOOR ||
                block.getType() == Material.JUNGLE_DOOR ||
                block.getType() == Material.SPRUCE_DOOR ||
                block.getType() == Material.DARK_OAK_DOOR ||
                block.getType() == Material.IRON_DOOR ||
                block.getType() == Material.TRAP_DOOR ||
                block.getType() == Material.IRON_TRAPDOOR ||
                block.getType() == Material.FENCE_GATE ||
                block.getType() == Material.ACACIA_FENCE_GATE ||
                block.getType() == Material.BIRCH_FENCE_GATE ||
                block.getType() == Material.JUNGLE_FENCE_GATE ||
                block.getType() == Material.SPRUCE_FENCE_GATE ||
                block.getType() == Material.DARK_OAK_FENCE_GATE;
    }

    public static boolean isButton(Block block) {
        return block.getType() == Material.STONE_BUTTON ||
                block.getType() == Material.WOOD_BUTTON ||
                block.getType() == Material.LEVER;
    }
}
