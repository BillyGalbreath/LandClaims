package net.pl3x.bukkit.claims;

import net.pl3x.bukkit.claims.claim.ClaimManager;
import net.pl3x.bukkit.claims.claim.task.DeleteInactiveClaims;
import net.pl3x.bukkit.claims.command.CmdAbandonAllClaims;
import net.pl3x.bukkit.claims.command.CmdAbandonClaim;
import net.pl3x.bukkit.claims.command.CmdAdjustAccruedBlocks;
import net.pl3x.bukkit.claims.command.CmdAdjustBonusBlocks;
import net.pl3x.bukkit.claims.command.CmdAdminClaims;
import net.pl3x.bukkit.claims.command.CmdBasicClaims;
import net.pl3x.bukkit.claims.command.CmdChildClaims;
import net.pl3x.bukkit.claims.command.CmdClaim;
import net.pl3x.bukkit.claims.command.CmdClaimBook;
import net.pl3x.bukkit.claims.command.CmdClaimsList;
import net.pl3x.bukkit.claims.command.CmdDeleteAllAdminClaimsInWorld;
import net.pl3x.bukkit.claims.command.CmdDeleteAllClaims;
import net.pl3x.bukkit.claims.command.CmdDeleteAllClaimsInWorld;
import net.pl3x.bukkit.claims.command.CmdDeleteAllUserClaimsInWorld;
import net.pl3x.bukkit.claims.command.CmdDeleteClaim;
import net.pl3x.bukkit.claims.command.CmdExtendClaim;
import net.pl3x.bukkit.claims.command.CmdIgnoreClaims;
import net.pl3x.bukkit.claims.command.CmdLandClaims;
import net.pl3x.bukkit.claims.command.CmdSetEntryMessage;
import net.pl3x.bukkit.claims.command.CmdSetExitMessage;
import net.pl3x.bukkit.claims.command.CmdSetFlag;
import net.pl3x.bukkit.claims.command.CmdTransferClaim;
import net.pl3x.bukkit.claims.command.CmdTrapped;
import net.pl3x.bukkit.claims.command.CmdTrust;
import net.pl3x.bukkit.claims.command.CmdTrustList;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.dynmap.DynmapHook;
import net.pl3x.bukkit.claims.listener.ClaimToolListener;
import net.pl3x.bukkit.claims.listener.FlagListener;
import net.pl3x.bukkit.claims.listener.PlayerListener;
import net.pl3x.bukkit.claims.listener.ProtectionListener;
import net.pl3x.bukkit.claims.listener.TrustListener;
import net.pl3x.bukkit.claims.pl3xmap.Pl3xMapHook;
import net.pl3x.bukkit.claims.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class LandClaims extends JavaPlugin {
    private static LandClaims instance;
    private final Logger logger;
    private final ClaimManager claimManager;
    private final PlayerManager playerManager;
    private DynmapHook dynmapHook;
    private Pl3xMapHook pl3xmapHook;

    public LandClaims() {
        instance = this;
        logger = new Logger(this);
        claimManager = new ClaimManager(this);
        playerManager = new PlayerManager(this);
    }

    public static LandClaims getInstance() {
        return instance;
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

        getServer().getPluginManager().registerEvents(new ClaimToolListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new FlagListener(this), this);
        getServer().getPluginManager().registerEvents(new TrustListener(this), this);

        getCommand("abandonallclaims").setExecutor(new CmdAbandonAllClaims(this));
        getCommand("abandonclaim").setExecutor(new CmdAbandonClaim(this));
        getCommand("adjustaccruedblocks").setExecutor(new CmdAdjustAccruedBlocks(this));
        getCommand("adjustbonusblocks").setExecutor(new CmdAdjustBonusBlocks(this));
        getCommand("adminclaims").setExecutor(new CmdAdminClaims(this));
        getCommand("basicclaims").setExecutor(new CmdBasicClaims(this));
        getCommand("childclaims").setExecutor(new CmdChildClaims(this));
        getCommand("claim").setExecutor(new CmdClaim(this));
        getCommand("claimbook").setExecutor(new CmdClaimBook(this));
        getCommand("claimslist").setExecutor(new CmdClaimsList(this));
        getCommand("deleteclaim").setExecutor(new CmdDeleteClaim(this));
        getCommand("deleteallclaims").setExecutor(new CmdDeleteAllClaims(this));
        getCommand("deleteallclaimsinworld").setExecutor(new CmdDeleteAllClaimsInWorld(this));
        getCommand("deletealladminclaimsinworld").setExecutor(new CmdDeleteAllAdminClaimsInWorld(this));
        getCommand("deletealluserclaimsinworld").setExecutor(new CmdDeleteAllUserClaimsInWorld(this));
        getCommand("extendclaim").setExecutor(new CmdExtendClaim(this));
        getCommand("ignoreclaims").setExecutor(new CmdIgnoreClaims(this));
        getCommand("landclaims").setExecutor(new CmdLandClaims(this));
        getCommand("setflag").setExecutor(new CmdSetFlag(this));
        getCommand("setentrymessage").setExecutor(new CmdSetEntryMessage(this));
        getCommand("setexitmessage").setExecutor(new CmdSetExitMessage(this));
        getCommand("transferclaim").setExecutor(new CmdTransferClaim(this));
        getCommand("trapped").setExecutor(new CmdTrapped(this));
        getCommand("trust").setExecutor(new CmdTrust(this));
        getCommand("trustlist").setExecutor(new CmdTrustList(this));

        getClaimManager().loadClaims();

        // Delete inactive claims task - delay 60 seconds, repeat 30 minutes
        new DeleteInactiveClaims(this).runTaskTimerAsynchronously(this, 600, 36000);

        // Dynmap hook
        if (getServer().getPluginManager().isPluginEnabled("Dynmap")) {
            getLog().info("Found Dynmap. Hooking claim markers...");
            dynmapHook = new DynmapHook(this);
        }

        // Pl3xMap hook
        if (getServer().getPluginManager().isPluginEnabled("Pl3xMap")) {
            getLog().info("Found Pl3xMap. Hooking claim markers...");
            pl3xmapHook = new Pl3xMapHook(this);
        }

        getLog().info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    public void onDisable() {
        if (dynmapHook != null) {
            dynmapHook.disable();
        }

        if (pl3xmapHook != null) {
            pl3xmapHook.disable();
        }

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
