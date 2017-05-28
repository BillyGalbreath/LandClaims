package net.pl3x.bukkit.claims.command;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.TrustType;
import net.pl3x.bukkit.claims.configuration.ClaimConfig;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CmdTrust implements TabExecutor {
    private final Pl3xClaims plugin;

    public CmdTrust(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    private enum CommandType {
        TRUST(Lang.TRUST_BUILD),
        CONTAINERTRUST(Lang.TRUST_CONTAINER),
        ACCESSTRUST(Lang.TRUST_ACCESS),
        PERMISSIONTRUST(Lang.TRUST_PERMISSION),
        UNTRUST(null);

        private final String desc;

        CommandType(String desc) {
            this.desc = desc;
        }

        String getName() {
            return name().toLowerCase();
        }

        String getDesc() {
            return desc;
        }

        static CommandType getType(String name) {
            return Arrays.stream(CommandType.values())
                    .filter(type -> type.getName().startsWith(name.toLowerCase()))
                    .findFirst().orElse(null);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> results = Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(offlinePlayer -> offlinePlayer.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(OfflinePlayer::getName).collect(Collectors.toList());
            results.add("all");
            results.add("public");
            return results;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        if (!sender.hasPermission("command.trust")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Player player = (Player) sender;
        if (Config.isWorldDisabled(player.getWorld())) {
            Lang.send(sender, Lang.WORLD_DISABLED);
            return true;
        }

        if (args.length == 0) {
            Lang.send(sender, Lang.COMMAND_MISSING_PLAYER);
            return false;
        }

        //noinspection deprecation
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null && !(args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("public"))) {
            Lang.send(sender, Lang.COMMAND_PLAYER_NOT_FOUND);
            return true;
        }

        Collection<Claim> targetClaims = new HashSet<>();
        Claim currentClaim = plugin.getClaimManager().getClaim(player.getLocation());
        if (currentClaim == null) {
            targetClaims.addAll(plugin.getPlayerManager().getPlayer(player).getClaims());
        } else {
            if (!currentClaim.allowManage(player.getUniqueId())) {
                Lang.send(sender, Lang.TRUST_ERROR_NO_PERMISSION);
                return true;
            }
            targetClaims.add(currentClaim);
        }

        if (targetClaims.isEmpty()) {
            Lang.send(sender, Lang.TRUST_ERROR_NO_CLAIM);
            return true;
        }

        CommandType commandType = CommandType.getType(label);
        if (commandType == null) {
            return false; // not sure if this is even possible
        }

        UUID targetUUID = target == null ? null : target.getUniqueId();
        String targetName = target == null ? Lang.TRUST_PUBLIC : target.getName();
        String targetLoc = targetClaims.size() == 1 ? Lang.TRUST_CURRENT_CLAIM : Lang.TRUST_ALL_CLAIMS;
        for (Claim targetClaim : targetClaims) {
            switch (commandType) {
                case TRUST:
                    targetClaim.setTrust(targetUUID, TrustType.BUILDER);
                    break;
                case CONTAINERTRUST:
                    targetClaim.setTrust(targetUUID, TrustType.CONTAINER);
                    break;
                case ACCESSTRUST:
                    targetClaim.setTrust(targetUUID, TrustType.ACCESS);
                    break;
                case PERMISSIONTRUST:
                    targetClaim.getManagers().add(targetUUID);
                    break;
                case UNTRUST:
                    targetClaim.setTrust(targetUUID, null);
                    targetClaim.getManagers().remove(targetUUID);
                    break;
            }

            ClaimConfig config = ClaimConfig.getConfig(plugin, targetClaim.getId());
            config.setTrusts(targetClaim.getTrusts());
            config.setManagers(targetClaim.getManagers());
            config.save();
        }

        Lang.send(sender, (commandType == CommandType.UNTRUST ? Lang.UNTRUST_SUCCESS : Lang.TRUST_SUCCESS)
                .replace("{target}", targetName)
                .replace("{location}", targetLoc)
                .replace("{desc}", commandType.getDesc()));

        return true;
    }
}
