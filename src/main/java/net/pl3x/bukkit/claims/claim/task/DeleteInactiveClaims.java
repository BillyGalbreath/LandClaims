package net.pl3x.bukkit.claims.claim.task;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import net.pl3x.bukkit.claims.player.Pl3xPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteInactiveClaims extends BukkitRunnable {
    private final LandClaims plugin;

    public DeleteInactiveClaims(LandClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (Config.DELETE_INACTIVE_CLAIMS <= 0) {
            return;
        }

        Set<Pl3xPlayer> targets = new HashSet<>(plugin.getClaimManager().getTopLevelClaims()).stream()
                .filter(claim -> claim.getLastActive() > Config.DELETE_INACTIVE_CLAIMS)
                .map(claim -> plugin.getPlayerManager().getPlayer(claim.getOwner()))
                .collect(Collectors.toSet());

        targets.forEach(target -> new DeleteClaims(target).runTaskLater(plugin, 1));
    }

    private class DeleteClaims extends BukkitRunnable {
        private final Pl3xPlayer pl3xPlayer;

        private DeleteClaims(Pl3xPlayer pl3xPlayer) {
            this.pl3xPlayer = pl3xPlayer;
        }

        @Override
        public void run() {
            Collection<Claim> claims = pl3xPlayer.getClaims();
            String name = pl3xPlayer.getPlayer().getName();
            int count = claims.size();

            claims.forEach(claim -> plugin.getClaimManager().deleteClaim(claim, true));

            for (Player player : Bukkit.getOnlinePlayers()) {
                Lang.send(player, Lang.DELETED_INACTIVE_CLAIMS
                        .replace("{owner}", name)
                        .replace("{count}", Integer.toString(count)));
            }
            Lang.send(Bukkit.getConsoleSender(), Lang.DELETED_INACTIVE_CLAIMS
                    .replace("{owner}", name)
                    .replace("{count}", Integer.toString(count)));
        }
    }
}
