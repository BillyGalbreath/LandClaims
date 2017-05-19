package net.pl3x.bukkit.claims.claim.group;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class Members {
    private final Collection<UUID> members = new HashSet<>();

    public void add(Player player) {
        add(player.getUniqueId());
    }

    public void add(UUID uuid) {
        members.add(uuid);
    }

    public void remove(UUID uuid) {
        members.remove(uuid);
    }

    public void remove(Player player) {
        remove(player.getUniqueId());
    }

    public boolean contains(UUID uuid) {
        return members.contains(uuid);
    }

    public boolean contains(Player player) {
        return contains(player.getUniqueId());
    }
}
