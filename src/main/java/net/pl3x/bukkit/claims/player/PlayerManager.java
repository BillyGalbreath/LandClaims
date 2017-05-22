package net.pl3x.bukkit.claims.player;

import net.pl3x.bukkit.claims.Pl3xClaims;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private final Pl3xClaims plugin;
    private final Map<UUID, Pl3xPlayer> players = new HashMap<>();

    public PlayerManager(Pl3xClaims plugin) {
        this.plugin = plugin;
    }

    public Pl3xPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public Pl3xPlayer getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            players.put(uuid, new Pl3xPlayer(plugin, Bukkit.getPlayer(uuid)));
        }
        return players.get(uuid);
    }

    public void unload(Player player) {
        unload(player.getUniqueId());
    }

    public void unload(UUID uuid) {
        if (players.containsKey(uuid)) {
            players.get(uuid).unload();
            players.remove(uuid);
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
