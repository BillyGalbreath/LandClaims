package net.pl3x.bukkit.claims;

import net.pl3x.bukkit.claims.claim.ClaimManager;
import net.pl3x.bukkit.claims.command.CmdPl3xClaims;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.listener.PlayerListener;
import net.pl3x.bukkit.claims.listener.ProtectionListener;
import net.pl3x.bukkit.claims.listener.ClaimToolListener;
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

        getServer().getPluginManager().registerEvents(new ClaimToolListener(this), this);
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
