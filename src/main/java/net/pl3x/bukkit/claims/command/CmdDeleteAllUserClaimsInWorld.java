package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CmdDeleteAllUserClaimsInWorld implements TabExecutor {
    private final LandClaims plugin;

    public CmdDeleteAllUserClaimsInWorld(LandClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getWorlds().stream()
                    .filter(world -> world.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(World::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Lang.send(sender, Lang.CONSOLE_COMMAND);
            return true;
        }

        if (!sender.hasPermission("command.deletealluserclaimsinworld")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            Lang.send(sender, Lang.COMMAND_MISSING_WORLD);
            return true;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            Lang.send(sender, Lang.COMMAND_WORLD_NOT_FOUND);
            return true;
        }

        Collection<Claim> claims = plugin.getClaimManager().getTopLevelClaims().stream()
                .filter(claim -> claim.getCoordinates().getWorld().equals(world))
                .filter(claim -> !claim.isAdminClaim())
                .collect(Collectors.toSet());

        if (claims == null || claims.isEmpty()) {
            Lang.send(sender, Lang.DELETE_ALL_USER_CLAIMS_IN_WORLD_NO_CLAIMS
                    .replace("{world}", world.getName()));
            return true;
        }

        claims.forEach(claim -> plugin.getClaimManager().deleteClaim(claim, true));
        Lang.send(sender, Lang.DELETE_ALL_USER_CLAIMS_IN_WORLD_SUCCESS
                .replace("{world}", world.getName()));
        return true;
    }
}
