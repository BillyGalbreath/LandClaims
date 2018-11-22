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

public class CmdSetExitMessage implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdSetExitMessage(Pl3xClaims plugin) {
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

        if (!sender.hasPermission("command.setexitmessage")) {
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
            Lang.send(sender, Lang.SETEXITMESSAGE_NO_CLAIM);
            return true;
        }

        if (!claim.allowManage(player)) {
            Lang.send(sender, Lang.SETFLAG_NO_MANAGE);
            return true;
        }

        String exitMessage = null;
        if (args.length > 0) {
            exitMessage = String.join(" ", args);
        }

        claim.setExitMessage(exitMessage);
        ClaimConfig config = ClaimConfig.getConfig(plugin, claim.getId());
        config.setExitMessage(exitMessage);
        config.save();

        Lang.send(sender, (exitMessage == null ? Lang.SETEXITMESSAGE_REMOVED : Lang.SETEXITMESSAGE_SUCCESS));
        return true;
    }
}
