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

import java.util.Collections;
import java.util.List;

public class CmdAbandonClaim implements TabExecutor {
    private final LandClaims plugin;

    public CmdAbandonClaim(LandClaims plugin) {
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

        if (!sender.hasPermission("command.abandonclaim")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Player player = (Player) sender;
        if (Config.isWorldDisabled(player.getWorld())) {
            Lang.send(sender, Lang.WORLD_DISABLED);
            return true;
        }

        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player);

        // which claim is being abandoned?
        Claim claim = plugin.getClaimManager().getClaim(player.getLocation());
        if (claim == null) {
            Lang.send(sender, Lang.ABANDON_CLAIM_MISSING);
            return true;
        }

        // verify ownership
        if (!claim.allowEdit(player)) {
            Lang.send(sender, Lang.NOT_YOUR_CLAIM);
            return true;
        }

        // warn if has children and we're not force deleting a top level claim
        boolean forceDelete = label.equalsIgnoreCase("abandontoplevelclaim") ||
                (args.length > 0 && "force".startsWith(args[0].toLowerCase()));
        if (claim.getChildren().size() > 0 && !forceDelete) {
            Lang.send(sender, Lang.ABANDON_TOP_LEVEL_CLAIM);
            return false; // show usage
        }

        Claim parent = claim.getParent();

        // delete it
        plugin.getClaimManager().deleteClaim(claim, forceDelete);

        // tell the player how many claim blocks he has left
        Lang.send(sender, Lang.ABANDON_SUCCESS
                .replace("{remaining}", Integer.toString(pl3xPlayer.getRemainingClaimBlocks())));

        //revert any current visualization

        if (parent != null && !parent.getChildren().isEmpty()) {
            claim = parent.getChildren().stream().findFirst().orElse(null);
            if (claim != null) {
                pl3xPlayer.showVisualization(claim);
                return true;
            }
        }

        pl3xPlayer.revertVisualization();
        return true;
    }
}
