package net.pl3x.bukkit.cities;

import net.pl3x.bukkit.cities.claim.CityManager;
import net.pl3x.bukkit.cities.command.CmdPl3xCities;
import net.pl3x.bukkit.cities.configuration.Config;
import net.pl3x.bukkit.cities.configuration.Lang;
import net.pl3x.bukkit.cities.hook.VaultHook;
import net.pl3x.bukkit.cities.listener.PlayerListener;
import net.pl3x.bukkit.cities.listener.ProtectionListener;
import net.pl3x.bukkit.cities.listener.RegionToolListener;
import net.pl3x.bukkit.cities.player.Pl3xPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xCities extends JavaPlugin {
    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        Logger.info("&3                                      ▄▄▄█████▄▄▄                               ");
        Logger.info("&3                                   ▄███▀▀▀   ▀▀▀███▄                            ");
        Logger.info("&3                                ▄██▀▀             ▀▀██▄                         ");
        Logger.info("&3                              ▄██▀                   ▀██▄                       ");
        Logger.info("&3 ▄███████████▄▄   ▄▄         ██▌ ▄██████████████████▄  ▐██  ▄▄▄             ▄▄▄ ");
        Logger.info("&3             ▀██ ▐██        ▐██                    ▀██  ██▌   ▀██▄       ▄██▀   ");
        Logger.info("&3 ▄▄▄▄▄▄▄▄▄▄▄▄███ ▐██        ██▌  ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄██▌ ▐██     ▀██ ▄▄▄ ██▀     ");
        Logger.info("&3▐███▀▀▀▀▀▀▀▀▀▀▀  ▐██        ██▌  ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀██ ▐██     ▄██ ▀▀▀ ██▄     ");
        Logger.info("&3▐██              ▐██▌       ▐██                     ▄██ ██▌   ▄██▀       ▀██▄   ");
        Logger.info("&3 ▀█               ▀█████████▄   ▀█████████████████████ ███  ▀▀▀             ▀▀▀ ");
        Logger.info("&3                              ▀██▄                   ▄██▀                       ");
        Logger.info("&3                                ▀██▄▄             ▄▄██▀                         ");
        Logger.info("&3                                   ▀███▄▄▄   ▄▄▄███▀                  Pl3x&oCities");
        Logger.info("&3                                      ▀▀▀█████▀▀▀                          ©2017");

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            Logger.info("Paper version: " + Bukkit.getVersion());
        } catch (ClassNotFoundException e) {
            Logger.error("Missing needed Paper API!");
            Logger.error(getName() + " is only compatible with Paper servers!");
            Logger.error("https://ci.destroystokyo.com/job/PaperSpigot/");
            Logger.warn(getName() + " will now disable itself.");
            return;
        }

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            Logger.info("Hooked into Vault");
        } else {
            Logger.error("Vault NOT found and/or enabled!");
            Logger.error(getName() + " requires Vault to be installed and enabled!");
            Logger.error("https://dev.bukkit.org/projects/vault");
            Logger.warn(getName() + " will now disable itself.");
            return;
        }

        if (VaultHook.getInstance().setupEconomy()) {
            Logger.info("Found a valid economy plugin via Vault");
        } else {
            Logger.error("No economy plugin found or installed!");
            Logger.error("This plugin requires a Vault compatible Economy plugin to be installed!");
            Logger.warn(getName() + " will now disable itself.");
            return;
        }

        getServer().getPluginManager().registerEvents(new RegionToolListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);

        getCommand("pl3xcities").setExecutor(new CmdPl3xCities(this));

        CityManager.getInstance().loadCities();

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    public void onDisable() {
        CityManager.getInstance().unloadCities();

        Pl3xPlayer.unloadAll();

        Logger.info(getName() + " disabled.");
    }

    public static Pl3xCities getPlugin() {
        return Pl3xCities.getPlugin(Pl3xCities.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.DARK_RED + getName() + " is disabled. Please check console logs for more information.");
        return true;
    }
}
