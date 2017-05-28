package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.flag.FlagType;
import net.pl3x.bukkit.claims.configuration.ClaimConfig;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CmdSetFlag implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdSetFlag(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    public enum FlagValue {
        ALLOW(true),
        TRUE(true),
        YES(true),
        ON(true),
        DENY(false),
        FALSE(false),
        NO(false),
        OFF(false),
        NONE(null),
        NULL(null);

        private final Boolean value;

        FlagValue(Boolean value) {
            this.value = value;
        }

        public static Boolean getValue(String name) {
            return Arrays.stream(values())
                    .filter(value -> value.name().toLowerCase().startsWith(name.toLowerCase()))
                    .findFirst().map(value -> value.value).orElse(null);
        }
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

        if (!sender.hasPermission("command.setflag")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Player player = (Player) sender;
        if (Config.isWorldDisabled(player.getWorld())) {
            Lang.send(sender, Lang.WORLD_DISABLED);
            return true;
        }

        Claim claim = plugin.getClaimManager().getClaim(player.getLocation());
        if (claim == null) {
            Lang.send(sender, Lang.SETFLAG_NO_CLAIM);
            return true;
        }

        if (args.length == 0) {
            Lang.send(sender, Lang.COMMAND_MISSING_FLAG);
            return true;
        }

        FlagType flag = FlagType.getType(args[0]);
        if (flag == null) {
            Lang.send(sender, Lang.COMMAND_FLAG_NOT_FOUND);
            return true;
        }

        if (!claim.allowManage(player)) {
            Lang.send(sender, Lang.SETFLAG_NO_PERMISSION);
            return true;
        }

        Boolean value = null;
        if (args.length > 1) {
            value = FlagValue.getValue(args[1]);
        }

        claim.setFlag(flag, value);
        ClaimConfig config = ClaimConfig.getConfig(plugin, claim.getId());
        config.setFlags(claim.getFlags());
        config.save();

        Lang.send(sender, (value == null ? Lang.SETFLAG_REMOVED : Lang.SETFLAG_SUCCESS)
                .replace("{flag}", flag.name().toLowerCase())
                .replace("{value}", value == null ? "null" : (value ? "allow" : "deny")));
        return true;
    }
}
