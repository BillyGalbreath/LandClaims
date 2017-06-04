package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.TrustType;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CmdTrustList implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdTrustList(Pl3xClaims plugin) {
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

        if (!sender.hasPermission("command.trustlist")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
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

        Collection<String> builders = new HashSet<>();
        Collection<String> containers = new HashSet<>();
        Collection<String> accessors = new HashSet<>();
        Collection<String> managers = new HashSet<>();

        claim.getTrusts().forEach((uuid, trustType) -> {
            String targetName = null;
            if (uuid == null) {
                targetName = Lang.TRUST_PUBLIC;
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                if (target != null) {
                    targetName = target.getName();
                }
            }
            if (targetName != null) {
                if (trustType == TrustType.BUILDER) {
                    builders.add(targetName);
                } else if (trustType == TrustType.CONTAINER) {
                    containers.add(targetName);
                } else {
                    accessors.add(targetName);
                }
            }
        });

        claim.getManagers().forEach(uuid -> {
            String targetName = null;
            if (uuid == null) {
                targetName = Lang.TRUST_PUBLIC;
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                if (target != null) {
                    targetName = target.getName();
                }
            }
            managers.add(targetName);
        });

        Lang.send(sender, Lang.TRUSTLIST_HEADER);
        Lang.send(sender, ChatColor.GOLD + "> " + String.join(", ", managers));
        Lang.send(sender, ChatColor.YELLOW + "> " + String.join(", ", builders));
        Lang.send(sender, ChatColor.GREEN + "> " + String.join(", ", containers));
        Lang.send(sender, ChatColor.BLUE + "> " + String.join(", ", accessors));
        Lang.send(sender, ChatColor.GOLD + Lang.TRUSTLIST_MANAGERS + " " +
                ChatColor.YELLOW + Lang.TRUSTLIST_BUILDERS + " " +
                ChatColor.GREEN + Lang.TRUSTLIST_CONTAINERS + " " +
                ChatColor.BLUE + Lang.TRUSTLIST_ACCESSORS);
        return true;
    }
}
