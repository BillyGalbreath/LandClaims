package net.pl3x.bukkit.claims.configuration;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static boolean COLOR_LOGS = true;
    public static boolean DEBUG_MODE = false;
    public static String LANGUAGE_FILE = "lang-en.yml";

    public static List<String> ENABLED_WORLDS = new ArrayList<>();

    public static boolean SUPPLY_CLAIMBOOK = true;

    public static int CLAIMS_MIN_WIDTH = 5;
    public static int CLAIMS_MIN_AREA = 100;
    public static int CLAIMS_MAX_PER_PLAYER = -1;
    public static int CLAIMS_AUTO_RADIUS = 5;

    public static String CLAIM_TOOL_MATERIAL = "STICK";
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

    public static int DELETE_INACTIVE_CLAIMS;

    public static int MAP_LAYER_PRIORITY;
    public static boolean MAP_LAYER_HIDEBYDEFAULT;
    public static int MAP_MIN_ZOOM;
    public static String MAP_TOOLIP;
    public static String MAP_ADMIN_TOOLTIP;
    public static boolean MAP_3D_REGIONS;
    public static List<String> MAP_VISIBLE_REGIONS;
    public static List<String> MAP_HIDDEN_REGIONS;

    public static boolean PREVENT_GLOBAL_CREEPER_POTHOLES = false;
    public static boolean PREVENT_GLOBAL_ENDERMAN_GRIEFING = false;

    private Config() {
    }

    public static void reload(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        COLOR_LOGS = config.getBoolean("color-logs", true);
        DEBUG_MODE = config.getBoolean("debug-mode", false);
        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");

        ENABLED_WORLDS = config.getStringList("enabled-worlds");

        SUPPLY_CLAIMBOOK = config.getBoolean("supply-claimbook", true);

        CLAIMS_MIN_WIDTH = config.getInt("claims.minimum-width", 5);
        CLAIMS_MIN_AREA = config.getInt("claims.minimum-area", 100);
        CLAIMS_MAX_PER_PLAYER = config.getInt("claims.maximum-per-player", -1);
        CLAIMS_AUTO_RADIUS = config.getInt("claims.auto-radius", 5);

        CLAIM_TOOL_MATERIAL = config.getString("claim-tool.material", "STICK");
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

        DELETE_INACTIVE_CLAIMS = config.getInt("delete-inactive-claims", 30);

        MAP_LAYER_PRIORITY = config.getInt("dynmap.layer.priority", 2);
        MAP_LAYER_HIDEBYDEFAULT = config.getBoolean("dynmap.layer.hidebydefault", false);
        MAP_MIN_ZOOM = config.getInt("dynmap.minzoom", 0);
        MAP_TOOLIP = config.getString("dynmap.info-window", "<div class=\"infowindow\"><span style=\"font-weight:bold;\">%owner%</span>''s Claim<br/>%dimensions% = <span style=\"font-weight:bold;\">%area%</span><br/>Last Active: <span style=\"font-weight:bold;\">%lastactive%</span><br/><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span><br/><br/><span style=\"font-weight:bold;\">Flags:</span>%flags%</div>");
        MAP_ADMIN_TOOLTIP = config.getString("dynmap.admin-window", "<div class=\"infowindow\"><span style=\"font-weight:bold;\">Administrator</span> Claim<br/>%dimensions% = <span style=\"font-weight:bold;\">%area%</span><br/><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span><br/><br/><span style=\"font-weight:bold;\">Flags:</span>%flags%</div>");
        MAP_3D_REGIONS = config.getBoolean("use3dregions", false);

        MAP_VISIBLE_REGIONS = config.getStringList("visibleregions");
        MAP_HIDDEN_REGIONS = config.getStringList("hiddenregions");

        PREVENT_GLOBAL_CREEPER_POTHOLES = config.getBoolean("prevent-global-creeper-potholes", PREVENT_GLOBAL_CREEPER_POTHOLES);
        PREVENT_GLOBAL_ENDERMAN_GRIEFING = config.getBoolean("prevent-global-enderman-griefing", PREVENT_GLOBAL_ENDERMAN_GRIEFING);
    }

    public static boolean isWorldDisabled(World world) {
        return !isWorldEnabled(world.getName());
    }

    public static boolean isWorldEnabled(String name) {
        return ENABLED_WORLDS.contains(name);
    }

    public static boolean isClaimTool(ItemStack item) {
        if (item == null || item.getType().isEmpty()) {
            return false; // no item
        }
        if (!item.getType().name().equals(Config.CLAIM_TOOL_MATERIAL)) {
            return false; // wrong material
        }
        ItemMeta meta = null;
        if (item.hasItemMeta()) {
            meta = item.getItemMeta();
            if (meta.hasCustomModelData()) {
                return false;
            }
        }
        if (Config.CLAIM_TOOL_NAME != null && !Config.CLAIM_TOOL_NAME.equals("")) {
            if (meta == null) {
                return false; // no item meta
            }
            if (!item.getItemMeta().getDisplayName().equals(Config.CLAIM_TOOL_NAME)) {
                return false; // name mismatch
            }
        }
        if (!Config.CLAIM_TOOL_LORE.isEmpty()) {
            if (meta == null) {
                return false; // no item meta
            }
            if (!meta.hasLore()) {
                return false; // has no lore
            }
            // lore mismatch
            return item.getItemMeta().getLore().containsAll(Config.CLAIM_TOOL_LORE);
        }
        return true;
    }

    public static boolean isInspectTool(ItemStack item) {
        if (item == null || item.getType().isEmpty()) {
            return false; // no item
        }
        if (!item.getType().name().equals(Config.INSPECT_TOOL_MATERIAL)) {
            return false; // wrong material
        }
        if (item.getData().getData() != Config.INSPECT_TOOL_DATA) {
            return false; // wrong data
        }
        ItemMeta meta = null;
        if (item.hasItemMeta()) {
            meta = item.getItemMeta();
            if (meta.hasCustomModelData()) {
                return false;
            }
        }
        if (Config.INSPECT_TOOL_NAME != null && !Config.INSPECT_TOOL_NAME.equals("")) {
            if (meta == null) {
                return false; // no item meta
            }
            if (!item.getItemMeta().getDisplayName().equals(Config.INSPECT_TOOL_NAME)) {
                return false; // name mismatch
            }
        }
        if (!Config.INSPECT_TOOL_LORE.isEmpty()) {
            if (meta == null) {
                return false; // no item meta
            }
            if (!meta.hasLore()) {
                return false; // has no lore
            }
            // lore mismatch
            return item.getItemMeta().getLore().containsAll(Config.INSPECT_TOOL_LORE);
        }
        return true;
    }
}
