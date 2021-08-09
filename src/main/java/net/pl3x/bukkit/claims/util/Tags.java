package net.pl3x.bukkit.claims.util;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import net.pl3x.bukkit.claims.LandClaims;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class Tags {
    public final static MaterialSetTag FARMABLE = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "farmable")))
            .add(Material.MELON, Material.PUMPKIN, Material.WHEAT, Material.BEETROOT, Material.POTATO,
                    Material.CARROT, Material.COCOA, Material.SUGAR_CANE, Material.NETHER_WART_BLOCK,
                    Material.CACTUS, Material.WHEAT_SEEDS, Material.BEETROOT_SEEDS, Material.MELON_SEEDS,
                    Material.PUMPKIN_SEEDS, Material.NETHER_WART);
    public static final MaterialSetTag BOATS = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "boats")))
            .endsWith("_BOAT");
    public final static MaterialSetTag MINECARTS = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "minecarts")))
            .add(Material.MINECART).endsWith("_MINECART");
    public static final MaterialSetTag DOORS = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "doors")))
            .endsWith("_DOOR").endsWith("_TRAPDOOR").endsWith("_FENCE_GATE");
    public static final MaterialSetTag BUTTONS = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "buttons")))
            .endsWith("_BUTTON").add(Material.LEVER);
    public static final MaterialSetTag PORTAL = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "portal")))
            .endsWith("_PORTAL");
    public static final MaterialSetTag CONTAINER = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "container")))
            .add(Material.CHEST, Material.CHEST_MINECART, Material.ENDER_CHEST, Material.TRAPPED_CHEST,
                    Material.BEACON, Material.BREWING_STAND, Material.DISPENSER, Material.DROPPER, Material.HOPPER,
                    Material.HOPPER_MINECART, Material.FURNACE, Material.FURNACE_MINECART,
                    Material.CAULDRON, Material.JUKEBOX, Material.ANVIL, Material.CAKE, Material.BARREL, Material.BLAST_FURNACE)
            .add(MaterialTags.SHULKER_BOXES);
    public static final MaterialSetTag LOGS = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "logs")))
            .add(Material.ACACIA_LOG, Material.BIRCH_LOG, Material.OAK_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG,
                    Material.SPRUCE_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM);
    public static final MaterialSetTag COPPER = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "copper")))
            .add(Material.COPPER_BLOCK, Material.EXPOSED_COPPER, Material.WEATHERED_COPPER, Material.OXIDIZED_COPPER,
                    Material.CUT_COPPER, Material.EXPOSED_CUT_COPPER, Material.WEATHERED_CUT_COPPER, Material.OXIDIZED_CUT_COPPER,
                    Material.CUT_COPPER_STAIRS, Material.EXPOSED_CUT_COPPER_STAIRS, Material.WEATHERED_CUT_COPPER_STAIRS, Material.OXIDIZED_CUT_COPPER_STAIRS,
                    Material.CUT_COPPER_SLAB, Material.EXPOSED_CUT_COPPER_SLAB, Material.WEATHERED_CUT_COPPER_SLAB, Material.OXIDIZED_CUT_COPPER_SLAB,
                    Material.WAXED_COPPER_BLOCK, Material.WAXED_EXPOSED_COPPER, Material.WAXED_WEATHERED_COPPER, Material.WAXED_OXIDIZED_COPPER,
                    Material.WAXED_CUT_COPPER, Material.WAXED_EXPOSED_CUT_COPPER, Material.WAXED_WEATHERED_CUT_COPPER, Material.WAXED_OXIDIZED_CUT_COPPER,
                    Material.WAXED_CUT_COPPER_STAIRS, Material.WAXED_EXPOSED_CUT_COPPER_STAIRS, Material.WAXED_WEATHERED_CUT_COPPER_STAIRS, Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
                    Material.WAXED_CUT_COPPER_SLAB, Material.WAXED_EXPOSED_CUT_COPPER_SLAB, Material.WAXED_WEATHERED_CUT_COPPER_SLAB, Material.WAXED_OXIDIZED_CUT_COPPER_SLAB);
    public static final MaterialSetTag INTERACTABLE = (new MaterialSetTag(new NamespacedKey(LandClaims.getInstance(), "interactable")))
            .add(Material.NOTE_BLOCK, Material.REPEATER, Material.COMPARATOR, Material.DAYLIGHT_DETECTOR,
                    Material.DRAGON_EGG, Material.FLOWER_POT, Material.END_CRYSTAL, Material.INK_SAC,
                    Material.ARMOR_STAND, Material.ITEM_FRAME, Material.EGG)
            .add(MaterialTags.DYES)
            .add(LOGS, COPPER)
            .endsWith("_DYE")
            .endsWith("_SPAWN_EGG");
}
