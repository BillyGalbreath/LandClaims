package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.visualization.VisualizationType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdDeleteClaim implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdDeleteClaim(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "force".startsWith(args[0].toLowerCase())) {
            return Collections.singletonList("force");
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        if (!sender.hasPermission("command.deleteclaim")) {
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
            Lang.send(sender, Lang.DELETE_NO_CLAIM);
            return true;
        }

        if (claim.isAdminClaim() && !sender.hasPermission("command.adminclaims")) {
            Lang.send(sender, Lang.DELETE_NO_PERMISSION);
            return true;
        }

        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player);
        boolean force = args.length > 0 && "force".startsWith(args[0].toLowerCase());
        if (!claim.getChildren().isEmpty() && !force) {
            Lang.send(sender, Lang.DELETE_HAS_CHILDREN);
            pl3xPlayer.showVisualization(claim, VisualizationType.ERROR);
            return false;
        }

        plugin.getClaimManager().deleteClaim(claim, force);
        Lang.send(sender, Lang.DELETE_SUCCESS);
        pl3xPlayer.revertVisualization();
        return true;
    }
}
