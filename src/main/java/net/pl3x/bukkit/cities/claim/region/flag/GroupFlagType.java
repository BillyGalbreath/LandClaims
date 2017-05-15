package net.pl3x.bukkit.cities.claim.region.flag;

public enum GroupFlagType {
    BUILD(false),
    INTERACT(false),
    CONTAINER(false),
    HURT_ANIMALS(false);

    private final boolean def;

    GroupFlagType(boolean def) {
        this.def = def;
    }

    public boolean getDefault() {
        return def;
    }
}
