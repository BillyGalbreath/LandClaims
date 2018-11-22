package net.pl3x.bukkit.claims.claim.flag;

import java.util.Arrays;

public enum FlagType {
    PVP(false),
    EXPLOSIONS(false),
    FIRESPREAD(false),
    SPAWN_ANIMALS(true),
    SPAWN_MOBS(true),
    MOB_DAMAGE(true),
    MOB_GRIEFING(false),
    ENTRY(true),
    EXIT(true);

    private final boolean def;

    FlagType(boolean def) {
        this.def = def;
    }

    public boolean getDefault() {
        return def;
    }

    public static FlagType getType(String name) {
        return Arrays.stream(FlagType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
