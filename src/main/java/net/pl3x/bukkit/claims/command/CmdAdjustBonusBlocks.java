package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CmdAdjustBonusBlocks implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdAdjustBonusBlocks(Pl3xClaims plugin) {
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
        if (!sender.hasPermission("command.adjustbonusblocks")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length < 1) {
            Lang.send(sender, Lang.COMMAND_MISSING_PLAYER);
            return false;
        }
        if (args.length < 2) {
            Lang.send(sender, Lang.COMMAND_MISSING_AMOUNT);
            return false;
        }

        Collection<Player> targetPlayers = new HashSet<>();
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            if (!"all".equalsIgnoreCase(args[0])) {
                Lang.send(sender, Lang.COMMAND_PLAYER_NOT_FOUND);
                return true;
            }
            targetPlayers.addAll(Bukkit.getOnlinePlayers());
        } else {
            targetPlayers.add(target);
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Lang.send(sender, Lang.COMMAND_NOT_A_NUMBER);
            return false;
        }

        targetPlayers.forEach(targetPlayer -> {
            Pl3xPlayer pl3xPlayer = plugin.getPlayerManager().getPlayer(targetPlayer);
            pl3xPlayer.setBonusBlocks(pl3xPlayer.getBonusBlocks() + amount);
        });

        Lang.send(sender, Lang.ADJUST_BONUS_BLOCKS_SUCCESS
                .replace("{target}", target == null ? Lang.EVERYONE : target.getName())
                .replace("{amount}", Integer.toString(amount)));

        return true;
    }
}
