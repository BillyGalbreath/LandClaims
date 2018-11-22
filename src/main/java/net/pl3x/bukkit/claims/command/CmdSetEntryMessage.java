package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.ClaimConfig;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdSetEntryMessage implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdSetEntryMessage(Pl3xClaims plugin) {
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

        if (!sender.hasPermission("command.setentrymessage")) {
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
            Lang.send(sender, Lang.SETENTRYMESSAGE_NO_CLAIM);
            return true;
        }

        if (!claim.allowManage(player)) {
            Lang.send(sender, Lang.SETFLAG_NO_MANAGE);
            return true;
        }

        String entryMessage = null;
        if (args.length > 0) {
            entryMessage = String.join(" ", args);
        }

        claim.setEntryMessage(entryMessage);
        ClaimConfig config = ClaimConfig.getConfig(plugin, claim.getId());
        config.setEntryMessage(entryMessage);
        config.save();

        Lang.send(sender, (entryMessage == null ? Lang.SETENTRYMESSAGE_REMOVED : Lang.SETENTRYMESSAGE_SUCCESS));
        return true;
    }
}
