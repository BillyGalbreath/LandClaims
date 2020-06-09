package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.LandClaims;
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
    private final LandClaims plugin;

    public CmdTransferClaim(LandClaims plugin) {
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
            Lang.send(sender, Lang.TRANSFER_ADMIN);
            return true;
        }

        if (!claim.isOwner(player)) {
            Lang.send(sender, Lang.NOT_YOUR_CLAIM);
            return true;
        }

        UUID owner = null;
        if (args.length > 0) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                Lang.send(sender, Lang.COMMAND_PLAYER_NOT_FOUND);
                return true;
            }
            owner = target.getUniqueId();
        }

        if (owner == null && !sender.hasPermission("command.admin.adminclaims")) {
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
