package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.Coordinates;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class CmdExtendClaim implements TabExecutor {
    private final LandClaims plugin;

    public CmdExtendClaim(LandClaims plugin) {
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

        if (!sender.hasPermission("command.extendclaim")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Player player = (Player) sender;
        if (Config.isWorldDisabled(player.getWorld())) {
            Lang.send(sender, Lang.WORLD_DISABLED);
            return true;
        }

        if (args.length < 1) {
            Lang.send(sender, Lang.COMMAND_MISSING_AMOUNT);
            return false;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            Lang.send(sender, Lang.COMMAND_NOT_A_NUMBER);
            return true;
        }

        Claim claim = plugin.getClaimManager().getClaim(player.getLocation());
        if (claim == null) {
            Lang.send(sender, Lang.EXTEND_NO_CLAIM);
            return true;
        }

        if (!claim.allowEdit(player)) {
            Lang.send(sender, Lang.NOT_YOUR_CLAIM);
            return true;
        }

        Vector direction = player.getLocation().getDirection();
        if (direction.getY() > .75) {
            Lang.send(sender, Lang.EXTEND_TO_SKY);
            return true;
        }

        if (direction.getY() < -.75) {
            Lang.send(sender, Lang.EXTEND_DOWNWARD);
            return true;
        }

        Location min = claim.getCoordinates().getMinLocation();
        Location max = claim.getCoordinates().getMaxLocation();
        int newMinX = min.getBlockX();
        int newMinZ = min.getBlockZ();
        int newMaxX = max.getBlockX();
        int newMaxZ = max.getBlockZ();

        //if changing Z only
        if (Math.abs(direction.getX()) < .3) {
            if (direction.getZ() > 0) {
                newMaxZ += amount;  //north
            } else {
                newMinZ -= amount;  //south
            }
        }

        //if changing X only
        else if (Math.abs(direction.getZ()) < .3) {
            if (direction.getX() > 0) {
                newMaxX += amount;  //east
            } else {
                newMinX -= amount;  //west
            }
        }

        //diagonals
        else {
            if (direction.getX() > 0) {
                newMaxX += amount;
            } else {
                newMinX -= amount;
            }

            if (direction.getZ() > 0) {
                newMaxZ += amount;
            } else {
                newMinZ -= amount;
            }
        }

        plugin.getClaimManager().resizeClaim(player, claim,
                new Coordinates(min.getWorld(), newMinX, newMinZ, newMaxX, newMaxZ));
        return true;
    }
}
