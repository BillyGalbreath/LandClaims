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

    public static String WILD_NAME = "Wilderness";
    public static Boolean WILD_PVP = true; // leave this as Boolean to prevent unboxing errors elsewhere

    public static int TITLE_TIME_FADE_IN = 10;
    public static int TITLE_TIME_STAY = 30;
    public static int TITLE_TIME_FADE_OUT = 20;

    public static String WAND_MATERIAL = "STICK";
    public static byte WAND_DATA = (byte) 0;
    public static String WAND_NAME = "STICK";
    public static List<String> WAND_LORE = new ArrayList<>();

    private Config() {
    }

    public static void reload() {
        Pl3xClaims plugin = Pl3xClaims.getPlugin();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        COLOR_LOGS = config.getBoolean("color-logs", true);
        DEBUG_MODE = config.getBoolean("debug-mode", false);
        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");

        ENABLED_WORLDS = config.getStringList("enabled-worlds");

        WILD_NAME = config.getString("wild.name", "Wilderness");
        WILD_PVP = config.getBoolean("wild.pvp", true);

        TITLE_TIME_FADE_IN = config.getInt("title.fade-in", 10);
        TITLE_TIME_STAY = config.getInt("title.stay", 30);
        TITLE_TIME_FADE_OUT = config.getInt("title.fade-out", 20);

        WAND_MATERIAL = config.getString("wand.material", "STICK");
        WAND_DATA = (byte) config.getInt("wand.data", 0);
        WAND_NAME = ChatColor.translateAlternateColorCodes('&',
                config.getString("wand.name", "Claim Tool"));
        WAND_LORE.clear();
        config.getStringList("wand.lore").forEach(lore ->
                WAND_LORE.add(ChatColor.translateAlternateColorCodes('&', lore)));

    }

    public static boolean isWorldDisabled(World world) {
        for (String name : ENABLED_WORLDS) {
            if (name.equalsIgnoreCase(world.getName())) {
                return false; // enabled
            }
        }
        return true; // disabled
    }

    public static boolean isRegionWand(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false; // no item
        }
        if (!item.getType().name().equals(Config.WAND_MATERIAL)) {
            return false; // wrong material
        }
        //noinspection deprecation
        if (item.getData().getData() != Config.WAND_DATA) {
            return false; // wrong data
        }
        if (Config.WAND_NAME != null && !Config.WAND_NAME.equals("")) {
            if (!item.hasItemMeta()) {
                return false; // no item meta
            }
            if (!item.getItemMeta().getDisplayName().equals(Config.WAND_NAME)) {
                return false; // name mismatch
            }
        }
        if (Config.WAND_LORE != null && !Config.WAND_LORE.isEmpty()) {
            if (!item.hasItemMeta()) {
                return false; // no item meta
            }
            for (String lore : Config.WAND_LORE) {
                if (!item.getItemMeta().getLore().contains(lore)) {
                    return false; // name mismatch
                }
            }
        }
        return true;
    }
}
