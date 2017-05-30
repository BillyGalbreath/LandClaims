package net.pl3x.bukkit.claims;

import net.pl3x.bukkit.claims.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Logger {
    private final JavaPlugin plugin;

    public Logger(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private void log(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&',
                "&3[&d" + plugin.getName() + "&3]&r " + msg);
        if (!Config.COLOR_LOGS) {
            msg = ChatColor.stripColor(msg);
        }
        Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }

    public void debug(String msg) {
        if (Config.DEBUG_MODE) {
            log("&7[&eDEBUG&7]&e " + msg);
        }
    }

    public void warn(String msg) {
        log("&e[&6WARN&e]&6 " + msg);
    }

    public void error(String msg) {
        log("&e[&4ERROR&e]&4 " + msg);
    }

    public void info(String msg) {
        log("&e[&fINFO&e]&r " + msg);
    }
}
