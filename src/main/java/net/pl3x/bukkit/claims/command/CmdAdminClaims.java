package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.player.ToolMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdAdminClaims implements TabExecutor {
    private final LandClaims plugin;

    public CmdAdminClaims(LandClaims plugin) {
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

        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player);
        pl3xPlayer.setToolMode(ToolMode.ADMIN);
        pl3xPlayer.setResizingClaim(null);
        pl3xPlayer.setParentClaim(null);
        pl3xPlayer.setLastToolLocation(null);

        Lang.send(sender, Lang.TOOLMODE_ADMIN);
        return true;
    }
}
