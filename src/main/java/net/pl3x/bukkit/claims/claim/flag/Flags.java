package net.pl3x.bukkit.claims.claim.flag;

import java.util.HashMap;
import java.util.Map;

public class Flags {
    private final Map<FlagType, Boolean> flags = new HashMap<>();

    public Boolean getFlag(FlagType flag) {
        return flags.get(flag);
    }

    public void setFlag(FlagType flag, Boolean value) {
        if (value == null) {
            flags.remove(flag);
            return;
        }

        flags.put(flag, value);
    }
}
