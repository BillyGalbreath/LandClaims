package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.util.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdTrustList implements TabExecutor {
    private final LandClaims plugin;

    public CmdTrustList(LandClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        Player player = (Player) sender;
        if (Config.isWorldDisabled(player.getWorld())) {
            Lang.send(sender, Lang.WORLD_DISABLED);
            return true;
        }

        Claim claim = plugin.getClaimManager().getClaim(player.getLocation());
        if (claim == null) {
            Lang.send(sender, Lang.TRUSTLIST_NO_CLAIM);
            return true;
        }

        if (!claim.allowManage(player)) {
            Lang.send(sender, Lang.TRUSTLIST_NO_PERMISSION);
            return true;
        }

        Permission perm = new Permission(claim);

        Lang.send(sender, Lang.TRUSTLIST_HEADER);
        Lang.send(sender, ChatColor.GOLD + "> " + String.join(", ", perm.managers()));
        Lang.send(sender, ChatColor.YELLOW + "> " + String.join(", ", perm.builders()));
        Lang.send(sender, ChatColor.GREEN + "> " + String.join(", ", perm.containers()));
        Lang.send(sender, ChatColor.BLUE + "> " + String.join(", ", perm.accessors()));
        Lang.send(sender, ChatColor.GOLD + Lang.TRUSTLIST_MANAGERS + " " +
                ChatColor.YELLOW + Lang.TRUSTLIST_BUILDERS + " " +
                ChatColor.GREEN + Lang.TRUSTLIST_CONTAINERS + " " +
                ChatColor.BLUE + Lang.TRUSTLIST_ACCESSORS);
        return true;
    }
}
