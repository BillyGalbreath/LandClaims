package net.pl3x.bukkit.claims.configuration;

import net.pl3x.bukkit.claims.Pl3xClaims;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMISSION;
    public static String PLAYER_COMMAND;
    public static String VERSION;
    public static String RELOAD;

    public static String INSPECT_NO_CLAIM;
    public static String INSPECT_TOO_FAR;

    public static String RESIZE_START;
    public static String RESIZE_SUCCESS;

    public static String CREATE_SUCCESS;

    private Lang() {
    }

    public static void reload(Pl3xClaims plugin) {
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        plugin.saveResource(Config.LANGUAGE_FILE, false);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&4You do not have permission for that command!");
        PLAYER_COMMAND = config.getString("player-command", "&4Player only command!");
        VERSION = config.getString("version", "&d{plugin} v{version}.");
        RELOAD = config.getString("reload", "&d{plugin} v{version} reloaded.");

        INSPECT_NO_CLAIM = config.getString("inspect-no-claim", "&4There are no claims here");
        INSPECT_TOO_FAR = config.getString("inspect-too-far", "&4That is too far away");

        RESIZE_START = config.getString("resize-start", "&dResizing claim. Use your tool again at the new location for this corner.");
        RESIZE_SUCCESS = config.getString("resize-success", "&dClaim resized. {amount} available claim blocks remaining.");

        CREATE_SUCCESS = config.getString("create-success", "&dClaim created!  Use /trust to share it with friends.");
    }

    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }
}
