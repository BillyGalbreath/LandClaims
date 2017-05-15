package net.pl3x.bukkit.cities.claim.region.group.trust;

public enum TrustType {
    BUILD(false),
    INTERACT(false),
    CONTAINER(false),
    HURT_ANIMALS(false);

    private final boolean def;

    TrustType(boolean def) {
        this.def = def;
    }

    public boolean getDefault() {
        return def;
    }
}
