package net.pl3x.bukkit.claims.configuration;

import net.pl3x.bukkit.claims.Pl3xClaims;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static boolean COLOR_LOGS = true;
    public static boolean DEBUG_MODE = false;
    public static String LANGUAGE_FILE = "lang-en.yml";

    public static List<String> ENABLED_WORLDS = new ArrayList<>();

    public static int CLAIM_MIN_WIDTH = 5;
    public static int CLAIM_MIN_AREA = 100;

    public static int MAX_CLAIMS_PER_PLAYER = -1;
    public static int AUTO_CLAIM_RADIUS = 5;

    public static String CLAIM_TOOL_MATERIAL = "STICK";
    public static byte CLAIM_TOOL_DATA = (byte) 0;
    public static String CLAIM_TOOL_NAME = "Claim Tool";
    public final static List<String> CLAIM_TOOL_LORE = new ArrayList<>();

    public static String INSPECT_TOOL_MATERIAL = "FEATHER";
    public static byte INSPECT_TOOL_DATA = (byte) 0;
    public static String INSPECT_TOOL_NAME = "Inspect Tool";
    public final static List<String> INSPECT_TOOL_LORE = new ArrayList<>();

    public static int STARTING_BLOCKS;
    public static int IDLE_THRESHOLD;
    public static int ACCRUED_PER_HOUR;
    public static int ACCRUED_IDLE_PERCENT;

    private Config() {
    }

    public static void reload(Pl3xClaims plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        COLOR_LOGS = config.getBoolean("color-logs", true);
        DEBUG_MODE = config.getBoolean("debug-mode", false);
        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");

        ENABLED_WORLDS = config.getStringList("enabled-worlds");

        CLAIM_MIN_WIDTH = config.getInt("claims.minimum-width", 5);
        CLAIM_MIN_AREA = config.getInt("claims.minimum-area", 100);
        MAX_CLAIMS_PER_PLAYER = config.getInt("claims.maximum-per-player", -1);
        AUTO_CLAIM_RADIUS = config.getInt("claims.auto-radius", 5);

        CLAIM_TOOL_MATERIAL = config.getString("claim-tool.material", "STICK");
        CLAIM_TOOL_DATA = (byte) config.getInt("claim-tool.data", 0);
        CLAIM_TOOL_NAME = ChatColor.translateAlternateColorCodes('&',
                config.getString("claim-tool.name", "Claim Tool"));
        CLAIM_TOOL_LORE.clear();
        config.getStringList("claim-tool.lore").forEach(lore ->
                CLAIM_TOOL_LORE.add(ChatColor.translateAlternateColorCodes('&', lore)));

        INSPECT_TOOL_MATERIAL = config.getString("inspect-tool.material", "FEATHER");
        INSPECT_TOOL_DATA = (byte) config.getInt("inspect-tool.data", 0);
        INSPECT_TOOL_NAME = ChatColor.translateAlternateColorCodes('&',
                config.getString("inspect-tool.name", "Inspect Tool"));
        INSPECT_TOOL_LORE.clear();
        config.getStringList("inspect-tool.lore").forEach(lore ->
                INSPECT_TOOL_LORE.add(ChatColor.translateAlternateColorCodes('&', lore)));

        STARTING_BLOCKS = config.getInt("claim-blocks.starting-amount", 100);
        IDLE_THRESHOLD = config.getInt("claim-blocks.idle-threshold", 3);
        ACCRUED_PER_HOUR = config.getInt("claim-blocks.accrued-per-hour", 100);
        ACCRUED_IDLE_PERCENT = config.getInt("claim-blocks.accrued-idle-percent", 50);
    }

    public static boolean isWorldDisabled(World world) {
        return !ENABLED_WORLDS.contains(world.getName());
    }

    public static boolean isClaimTool(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false; // no item
        }
        if (!item.getType().name().equals(Config.CLAIM_TOOL_MATERIAL)) {
            return false; // wrong material
        }
        //noinspection deprecation
        if (item.getData().getData() != Config.CLAIM_TOOL_DATA) {
            return false; // wrong data
        }
        if (Config.CLAIM_TOOL_NAME != null && !Config.CLAIM_TOOL_NAME.equals("")) {
            if (!item.hasItemMeta()) {
                return false; // no item meta
            }
            if (!item.getItemMeta().getDisplayName().equals(Config.CLAIM_TOOL_NAME)) {
                return false; // name mismatch
            }
        }
        if (Config.CLAIM_TOOL_LORE != null && !Config.CLAIM_TOOL_LORE.isEmpty()) {
            if (!item.hasItemMeta()) {
                return false; // no item meta
            }
            for (String lore : Config.CLAIM_TOOL_LORE) {
                if (!item.getItemMeta().getLore().contains(lore)) {
                    return false; // lore mismatch
                }
            }
        }
        return true;
    }

    public static boolean isInspectTool(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false; // no item
        }
        if (!item.getType().name().equals(Config.INSPECT_TOOL_MATERIAL)) {
            return false; // wrong material
        }
        //noinspection deprecation
        if (item.getData().getData() != Config.INSPECT_TOOL_DATA) {
            return false; // wrong data
        }
        if (Config.INSPECT_TOOL_NAME != null && !Config.INSPECT_TOOL_NAME.equals("")) {
            if (!item.hasItemMeta()) {
                return false; // no item meta
            }
            if (!item.getItemMeta().getDisplayName().equals(Config.INSPECT_TOOL_NAME)) {
                return false; // name mismatch
            }
        }
        if (Config.INSPECT_TOOL_LORE != null && !Config.INSPECT_TOOL_LORE.isEmpty()) {
            if (!item.hasItemMeta()) {
                return false; // no item meta
            }
            for (String lore : Config.INSPECT_TOOL_LORE) {
                if (!item.getItemMeta().getLore().contains(lore)) {
                    return false; // lore mismatch
                }
            }
        }
        return true;
    }
}
