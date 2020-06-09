package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class CmdAbandonAllClaims implements TabExecutor {
    private final LandClaims plugin;

    public CmdAbandonAllClaims(LandClaims plugin) {
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

        Collection<Claim> claims = pl3xPlayer.getClaims();
        if (claims == null || claims.isEmpty()) {
            Lang.send(sender, Lang.YOU_HAVE_NO_CLAIMS);
            return true;
        }

        claims.forEach(claim -> plugin.getClaimManager().deleteClaim(claim, true));

        Lang.send(sender, Lang.ABANDON_SUCCESS
                .replace("{remaining}", Integer.toString(pl3xPlayer.getRemainingClaimBlocks())));

        pl3xPlayer.revertVisualization();

        return true;
    }
}
