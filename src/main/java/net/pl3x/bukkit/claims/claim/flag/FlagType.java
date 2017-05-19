package net.pl3x.bukkit.claims.claim.flag;

public enum FlagType {
    PVP(false),
    EXPLOSIONS(false),
    FIRESPREAD(false),
    SPAWN_ANIMALS(true),
    SPAWN_MOBS(true),
    MOB_DAMAGE(true),
    MOB_GRIEFING(false);

    private final boolean def;

    FlagType(boolean def) {
        this.def = def;
    }

    public boolean getDefault() {
        return def;
    }
}
