package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.Coordinates;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.event.CreateClaimEvent;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdClaim implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdClaim(Pl3xClaims plugin) {
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

        // check max claims per player limit
        if (Config.MAX_CLAIMS_PER_PLAYER > 0 &&
                !player.hasPermission("claims.overridelimits") &&
                pl3xPlayer.getClaims().size() >= Config.MAX_CLAIMS_PER_PLAYER) {
            Lang.send(sender, Lang.CREATE_FAILED_CLAIM_LIMIT
                    .replace("{limit}", Integer.toString(Config.MAX_CLAIMS_PER_PLAYER)));
            return true;
        }

        // default is chest claim radius, unless -1
        int radius = Config.AUTO_CLAIM_RADIUS;
        if (radius < 0) {
            radius = (int) Math.ceil(Math.sqrt(Config.CLAIM_MIN_AREA) / 2);
        }

        // allow for specifying the radius
        if (args.length > 0) {
            int specifiedRadius;
            try {
                specifiedRadius = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return false;
            }

            if (specifiedRadius < radius) {
                Lang.send(sender, Lang.MIN_RADIUS
                        .replace("{radius}", Integer.toString(radius)));
                return true;
            }

            radius = specifiedRadius;
        }

        // verify not negative
        if (radius < 0) {
            radius = 0;
        }

        Coordinates newCoords = new Coordinates(
                player.getLocation().add(-radius, 0, -radius),
                player.getLocation().add(radius, 0, radius));

        // check if enough unused claims blocks
        int remaining = pl3xPlayer.getRemainingClaimBlocks();
        if (remaining < newCoords.getArea()) {
            Lang.send(sender, Lang.CREATE_FAILED_NEED_MORE_BLOCKS
                    .replace("{required}", Integer.toString(newCoords.getArea() - remaining)));
            return true;
        }

        // check for overlaps
        for (Claim topLevelClaim : plugin.getClaimManager().getTopLevelClaims()) {
            if (topLevelClaim.getCoordinates().overlaps(newCoords)) {
                Lang.send(player, Lang.CREATE_FAILED_OVERLAP);
                pl3xPlayer.showVisualization(topLevelClaim, VisualizationType.ERROR);
                return true;
            }
        }

        // create new claim
        Claim newClaim = new Claim(plugin.getClaimManager().getNextId(),
                player.getUniqueId(),
                null, newCoords, false);

        CreateClaimEvent createClaimEvent = new CreateClaimEvent(player, newClaim);
        Bukkit.getPluginManager().callEvent(createClaimEvent);
        if (createClaimEvent.isCancelled()) {
            return true; // cancelled by plugin
        }

        // store new claim
        plugin.getClaimManager().createNewClaim(newClaim);
        Lang.send(player, Lang.CREATE_SUCCESS);

        pl3xPlayer.showVisualization(newClaim);

        pl3xPlayer.setParentClaim(null);
        pl3xPlayer.setResizingClaim(null);
        pl3xPlayer.setLastToolLocation(null);

        return true;
    }
}
