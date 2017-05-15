package net.pl3x.bukkit.cities.claim.region.group;

public enum GroupType {
    MANAGER(3),
    MEMBER(2),
    NON_MEMBER(1),
    ALL(0);

    private final int weight;

    GroupType(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
