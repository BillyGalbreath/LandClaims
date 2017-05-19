package net.pl3x.bukkit.claims;

import net.pl3x.bukkit.claims.claim.ClaimManager;
import net.pl3x.bukkit.claims.command.CmdPl3xClaims;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.hook.VaultHook;
import net.pl3x.bukkit.claims.listener.PlayerListener;
import net.pl3x.bukkit.claims.listener.ProtectionListener;
import net.pl3x.bukkit.claims.listener.RegionToolListener;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xClaims extends JavaPlugin {
    private static Pl3xClaims instance;

    public static Pl3xClaims getPlugin() {
        return instance;
    }

    public Pl3xClaims() {
        instance = this;
    }

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
        Logger.info("&3                                   ▀███▄▄▄   ▄▄▄███▀                  Pl3x&oClaims");
        Logger.info("&3                                      ▀▀▀█████▀▀▀                          ©2017");

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

        getCommand("pl3xclaims").setExecutor(new CmdPl3xClaims(this));

        ClaimManager.getInstance().loadClaims();

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    public void onDisable() {
        ClaimManager.getInstance().unloadClaims();

        Pl3xPlayer.unloadAll();

        Logger.info(getName() + " disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.DARK_RED + getName() + " is disabled. Please check console logs for more information.");
        return true;
    }
}
