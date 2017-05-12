package net.pl3x.bukkit.cities;

import net.pl3x.bukkit.cities.commands.CmdPl3xCities;
import net.pl3x.bukkit.cities.hook.Vault;
import net.pl3x.bukkit.cities.listener.BukkitListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xCities extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            Class.forName("org.bukkit.event.entity.EntityPickupItemEvent");
            Item.class.getMethod("canEntityPickup", (Class<?>[]) null);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Logger.error("Missing needed classes/methods!");
            Logger.error("This plugin is only compatible with Paper servers!");
            return;
        }

        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            Logger.error("# Vault NOT found and/or enabled!");
            Logger.error("# " + getName() + " requires Vault to be installed and enabled!");
            return;
        }

        if (!Vault.setupEconomy()) {
            Logger.error("# No economy plugin installed!");
            Logger.error("# This plugin requires a Vault compatible Economy plugin to be installed!");
            return;
        }

        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);

        getCommand("pl3xcities").setExecutor(new CmdPl3xCities(this));

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    public void onDisable() {
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
