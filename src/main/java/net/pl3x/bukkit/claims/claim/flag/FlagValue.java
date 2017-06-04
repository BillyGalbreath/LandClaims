package net.pl3x.bukkit.claims.claim.flag;

import java.util.Arrays;

public enum FlagValue {
    ALLOW(true),
    TRUE(true),
    YES(true),
    ON(true),
    DENY(false),
    FALSE(false),
    NO(false),
    OFF(false),
    NONE(null),
    NULL(null);

    private final Boolean value;

    FlagValue(Boolean value) {
        this.value = value;
    }

    public static Boolean getValue(String name) {
        return Arrays.stream(values())
                .filter(value -> value.name().toLowerCase().startsWith(name.toLowerCase()))
                .findFirst().map(value -> value.value).orElse(null);
    }
}
