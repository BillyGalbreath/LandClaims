package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.Coordinates;
import net.pl3x.bukkit.claims.configuration.ClaimConfig;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.event.ResizeClaimEvent;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import net.pl3x.bukkit.claims.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class CmdExtendClaim implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdExtendClaim(Pl3xClaims plugin) {
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

        Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(player);
        pl3xPlayer.setResizingClaim(claim);

        Coordinates newCoords = new Coordinates(min.getWorld(), newMinX, newMinZ, newMaxX, newMaxZ);

        // check size rules
        // admin claims bypass this check
        if (claim.getParent() == null && !claim.isAdminClaim()) {
            // check minimum size requirements if shrinking
            if (!player.hasPermission("command.adminclaims") &&
                    (newCoords.getWidthX() < claim.getCoordinates().getWidthX() ||
                            newCoords.getWidthZ() < claim.getCoordinates().getWidthZ())) {
                if (newCoords.getWidthX() < Config.CLAIMS_MIN_WIDTH || newCoords.getWidthZ() < Config.CLAIMS_MIN_WIDTH) {
                    Lang.send(sender, Lang.RESIZE_FAILED_TOO_NARROW
                            .replace("{minimum}", Integer.toString(Config.CLAIMS_MIN_WIDTH)));
                    return true;
                }
                if (newCoords.getArea() < Config.CLAIMS_MIN_AREA) {
                    Lang.send(sender, Lang.RESIZE_FAILED_TOO_SMALL
                            .replace("{minimum}", Integer.toString(Config.CLAIMS_MIN_AREA)));
                    return true;
                }

            }

            // check if player has enough claim blocks
            int remaining = pl3xPlayer.getRemainingClaimBlocks() + claim.getCoordinates().getArea() - newCoords.getArea();
            if (remaining < 0) {
                Lang.send(sender, Lang.RESIZE_FAILED_NEED_MORE_BLOCKS
                        .replace("{amount}", Integer.toString(-remaining)));
                return true;
            }
        }

        // check for overlapping other claims
        for (Claim topLevelClaim : plugin.getClaimManager().getTopLevelClaims()) {
            if (topLevelClaim == claim) {
                continue;
            }
            if (topLevelClaim.getCoordinates().overlaps(newCoords)) {
                Lang.send(sender, Lang.RESIZE_FAILED_OVERLAP);
                pl3xPlayer.showVisualization(topLevelClaim, VisualizationType.ERROR);
                return true;
            }
        }

        ResizeClaimEvent resizeClaimEvent = new ResizeClaimEvent(player, claim);
        Bukkit.getPluginManager().callEvent(resizeClaimEvent);
        if (resizeClaimEvent.isCancelled()) {
            return true; // cancelled by another plugin
        }

        // resize the claim
        claim.getCoordinates().resize(newCoords);
        ClaimConfig claimConfig = ClaimConfig.getConfig(plugin, claim.getId());
        claimConfig.setCoordinates(newCoords);
        claimConfig.save();

        int remaining = pl3xPlayer.getRemainingClaimBlocks();
        UUID owner = claim.getParent() != null ? claim.getParent().getOwner() : claim.getOwner();
        if (!player.getUniqueId().equals(owner)) {
            remaining = plugin.getPlayerManager().getPlayer(owner).getRemainingClaimBlocks();
            if (!Bukkit.getOfflinePlayer(owner).isOnline()) {
                plugin.getPlayerManager().unload(owner);
            }
        }

        Lang.send(player, Lang.RESIZE_SUCCESS
                .replace("{amount}", Integer.toString(remaining)));
        pl3xPlayer.showVisualization(claim);

        pl3xPlayer.setLastToolLocation(null);
        pl3xPlayer.setResizingClaim(null);
        pl3xPlayer.setParentClaim(null);

        return true;
    }
}
