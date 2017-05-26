package net.pl3x.bukkit.claims;

import net.pl3x.bukkit.claims.claim.ClaimManager;
import net.pl3x.bukkit.claims.command.CmdAbandonAllClaims;
import net.pl3x.bukkit.claims.command.CmdAbandonClaim;
import net.pl3x.bukkit.claims.command.CmdAdminClaims;
import net.pl3x.bukkit.claims.command.CmdBasicClaims;
import net.pl3x.bukkit.claims.command.CmdChildClaims;
import net.pl3x.bukkit.claims.command.CmdClaim;
import net.pl3x.bukkit.claims.command.CmdPl3xClaims;
import net.pl3x.bukkit.claims.command.CmdTrust;
import net.pl3x.bukkit.claims.command.CmdTrustList;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.listener.ClaimToolListener;
import net.pl3x.bukkit.claims.listener.PlayerListener;
import net.pl3x.bukkit.claims.listener.ProtectionListener;
import net.pl3x.bukkit.claims.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xClaims extends JavaPlugin {
    private final Logger logger;
    private final ClaimManager claimManager;
    private final PlayerManager playerManager;

    public Pl3xClaims() {
        logger = new Logger(this);
        claimManager = new ClaimManager(this);
        playerManager = new PlayerManager(this);
    }

    public Logger getLog() {
        return logger;
    }

    public ClaimManager getClaimManager() {
        return claimManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public void onEnable() {
        Config.reload(this);
        Lang.reload(this);

        getLog().info("&3                                      ▄▄▄█████▄▄▄                               ");
        getLog().info("&3                                   ▄███▀▀▀   ▀▀▀███▄                            ");
        getLog().info("&3                                ▄██▀▀             ▀▀██▄                         ");
        getLog().info("&3                              ▄██▀                   ▀██▄                       ");
        getLog().info("&3 ▄███████████▄▄   ▄▄         ██▌ ▄██████████████████▄  ▐██  ▄▄▄             ▄▄▄ ");
        getLog().info("&3             ▀██ ▐██        ▐██                    ▀██  ██▌   ▀██▄       ▄██▀   ");
        getLog().info("&3 ▄▄▄▄▄▄▄▄▄▄▄▄███ ▐██        ██▌  ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄██▌ ▐██     ▀██ ▄▄▄ ██▀     ");
        getLog().info("&3▐███▀▀▀▀▀▀▀▀▀▀▀  ▐██        ██▌  ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀██ ▐██     ▄██ ▀▀▀ ██▄     ");
        getLog().info("&3▐██              ▐██▌       ▐██                     ▄██ ██▌   ▄██▀       ▀██▄   ");
        getLog().info("&3 ▀█               ▀█████████▄   ▀█████████████████████ ███  ▀▀▀             ▀▀▀ ");
        getLog().info("&3                              ▀██▄                   ▄██▀                       ");
        getLog().info("&3                                ▀██▄▄             ▄▄██▀                         ");
        getLog().info("&3                                   ▀███▄▄▄   ▄▄▄███▀                  Pl3x&oClaims");
        getLog().info("&3                                      ▀▀▀█████▀▀▀                          ©2017");

        getServer().getPluginManager().registerEvents(new ClaimToolListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);

        getCommand("abandonclaim").setExecutor(new CmdAbandonClaim(this));
        getCommand("abandonallclaims").setExecutor(new CmdAbandonAllClaims(this));
        getCommand("claim").setExecutor(new CmdClaim(this));
        getCommand("adminclaims").setExecutor(new CmdAdminClaims(this));
        getCommand("basicclaims").setExecutor(new CmdBasicClaims(this));
        getCommand("childclaims").setExecutor(new CmdChildClaims(this));
        getCommand("trust").setExecutor(new CmdTrust(this));
        getCommand("trustlist").setExecutor(new CmdTrustList(this));
        getCommand("pl3xclaims").setExecutor(new CmdPl3xClaims(this));

        getClaimManager().loadClaims();

        getLog().info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    public void onDisable() {
        getClaimManager().unloadClaims();

        getPlayerManager().unloadAll();

        getLog().info(getName() + " disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.DARK_RED + getName() + " is disabled. Please check console logs for more information.");
        return true;
    }
}
