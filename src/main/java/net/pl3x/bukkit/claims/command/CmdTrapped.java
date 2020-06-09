package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.event.player.SaveTrappedPlayerEvent;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.player.task.SaveTrappedPlayerTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdTrapped implements TabExecutor {
    private final LandClaims plugin;

    public CmdTrapped(LandClaims plugin) {
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
        if (pl3xPlayer.hasPendingRescue()) {
            return true;
        }

        Claim claim = plugin.getClaimManager().getClaim(player.getLocation());
        if (claim == null || claim.allowBuild(player)) {
            Lang.send(sender, Lang.TRAPPED_CAN_BUILD);
            return true;
        }

        SaveTrappedPlayerEvent saveTrappedPlayerEvent = new SaveTrappedPlayerEvent(player, claim);
        Bukkit.getPluginManager().callEvent(saveTrappedPlayerEvent);
        if (saveTrappedPlayerEvent.isCancelled()) {
            return true; // cancelled by plugin
        }

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL &&
                saveTrappedPlayerEvent.getDestination() == null) {
            Lang.send(sender, Lang.TRAPPED_WONT_WORK_HERE);
            return true;
        }

        Lang.send(sender, Lang.TRAPPED_RESCUE_PENDING);
        pl3xPlayer.setPendingRescue(true);

        new SaveTrappedPlayerTask(pl3xPlayer, player.getLocation(),
                saveTrappedPlayerEvent.getDestination())
                .runTaskLater(plugin, 200L); // 10 second delay
        return true;
    }
}
