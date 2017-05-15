package net.pl3x.bukkit.cities.configuration;

import net.pl3x.bukkit.cities.Pl3xCities;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

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

    private Config() {
    }

    public static void reload() {
        Pl3xCities plugin = Pl3xCities.getPlugin();
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
    }

    public static boolean isWorldDisabled(World world) {
        for (String name : ENABLED_WORLDS) {
            if (name.equalsIgnoreCase(world.getName())) {
                return false; // enabled
            }
        }
        return true; // disabled
    }
}
