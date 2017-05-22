package net.pl3x.bukkit.claims.player;

import net.pl3x.bukkit.claims.Pl3xClaims;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private final Pl3xClaims plugin;
    private final Map<Player, Pl3xPlayer> players = new HashMap<>();

    public PlayerManager(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    public Pl3xPlayer getPlayer(Player player) {
        if (!players.containsKey(player)) {
            players.put(player, new Pl3xPlayer(plugin, player));
        }
        return players.get(player);
    }

    public void unload(Player player) {
        if (players.containsKey(player)) {
            players.get(player).unload();
            players.remove(player);
        }
    }

    public void unloadAll() {
        players.values().forEach(Pl3xPlayer::unload);
        players.clear();
    }

    public void reloadAll() {
        players.values().forEach(Pl3xPlayer::reload);
    }
}
