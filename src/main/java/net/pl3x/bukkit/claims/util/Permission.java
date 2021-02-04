package net.pl3x.bukkit.claims.util;

import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.TrustType;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class Permission {
    private final Collection<String> builders = new HashSet<>();
    private final Collection<String> containers = new HashSet<>();
    private final Collection<String> accessors = new HashSet<>();
    private final Collection<String> managers = new HashSet<>();

    public Permission(Claim claim) {
        claim.getTrusts().forEach((uuid, trustType) -> {
            if (trustType == TrustType.BUILDER) {
                builders.add(getName(uuid));
            } else if (trustType == TrustType.CONTAINER) {
                containers.add(getName(uuid));
            } else {
                accessors.add(getName(uuid));
            }
        });

        claim.getManagers().forEach(uuid -> {
            managers.add(getName(uuid));
        });
    }

    public Collection<String> builders() {
        return builders;
    }

    public Collection<String> containers() {
        return containers;
    }

    public Collection<String> accessors() {
        return accessors;
    }

    public Collection<String> managers() {
        return managers;
    }

    private String getName(UUID uuid) {
        if (uuid.equals(Claim.PUBLIC_UUID)) {
            return Lang.TRUST_PUBLIC;
        } else {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
    }
}
