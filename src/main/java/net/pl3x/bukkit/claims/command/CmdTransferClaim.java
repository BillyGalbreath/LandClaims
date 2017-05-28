package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.ClaimConfig;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CmdTransferClaim implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdTransferClaim(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(target -> target.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        if (!sender.hasPermission("command.transferclaim")) {
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
            Lang.send(sender, Lang.TRANSFER_NO_CLAIM);
            return true;
        }

        if (claim.getParent() != null) {
            Lang.send(sender, Lang.TRANSFER_CHILD);
            return true;
        }

        if (claim.isAdminClaim()) {
            if (!sender.hasPermission("command.adminclaims")) {
                Lang.send(sender, Lang.TRANSFER_NO_PERMISSION);
                return true;
            }
        } else {
            if (!claim.isOwner(player)) {
                Lang.send(sender, Lang.NOT_YOUR_CLAIM);
                return true;
            }
        }

        UUID owner = null;
        if (args.length > 0) {
            //noinspection deprecation
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                Lang.send(sender, Lang.PLAYER_NOT_FOUND);
                return true;
            }
            owner = target.getUniqueId();
        }

        if (owner == null && !sender.hasPermission("command.adminclaims")) {
            Lang.send(sender, Lang.TRANSFER_NO_PERMISSION);
            return true;
        }

        claim.setOwner(owner);
        claim.setAdminClaim(owner == null);
        ClaimConfig config = ClaimConfig.getConfig(plugin, claim.getId());
        config.setOwner(claim.getOwner());
        config.setAdminClaim(claim.isAdminClaim());
        config.save();

        Lang.send(sender, Lang.TRANSFER_SUCCESS);
        return true;
    }
}
