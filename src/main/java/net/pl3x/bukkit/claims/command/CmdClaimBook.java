package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.task.WelcomeTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdClaimBook implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdClaimBook(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(target -> target.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.claimbook")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (!Config.SUPPLY_CLAIMBOOK) {
            Lang.send(sender, Lang.CLAIMBOOK_DISABLED);
            return true;
        }

        Player target;
        if (args.length > 0) {
            if (!sender.hasPermission("command.claimbook.others")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            //noinspection deprecation
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Lang.send(sender, Lang.COMMAND_PLAYER_NOT_FOUND);
                return true;
            }

            Lang.send(sender, Lang.CLAIMBOOK_GIVEN
                    .replace("{target}", target.getName()));
        } else {
            if (!(sender instanceof Player)) {
                Lang.send(sender, Lang.PLAYER_COMMAND);
                return true;
            }
            target = (Player) sender;
        }

        new WelcomeTask(target).run();
        return true;
    }
}
