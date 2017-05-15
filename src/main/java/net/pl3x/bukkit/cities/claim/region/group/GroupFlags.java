package net.pl3x.bukkit.cities.claim.region.group;

import net.pl3x.bukkit.cities.claim.region.flag.GroupFlagType;

import java.util.HashMap;
import java.util.Map;

public class GroupFlags {
    private final Map<GroupFlagType, Boolean> flags = new HashMap<>();

    public Boolean getFlag(GroupFlagType flag) {
        return flags.get(flag);
    }

    public void setFlag(GroupFlagType flag, Boolean value) {
        if (value == null) {
            flags.remove(flag);
            return;
        }
        flags.put(flag, value);
    }
}
