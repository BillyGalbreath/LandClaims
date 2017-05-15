package net.pl3x.bukkit.cities.claim.region.group;

import net.pl3x.bukkit.cities.claim.region.flag.GroupFlagType;
import net.pl3x.bukkit.cities.claim.region.flag.GroupType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionGroups {
    private final UUID owner;
    private final Map<GroupType, Members> groupMembers = new HashMap<>();
    private final Map<GroupType, GroupFlags> groupFlags = new HashMap<>();

    public RegionGroups(UUID owner) {
        this.owner = owner;
    }

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(owner);
    }

    public GroupType getGroup(Player player) {
        for (Map.Entry<GroupType, Members> entry : groupMembers.entrySet()) {
            if (entry.getValue().contains(player)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void setGroup(Player player, GroupType group) {
        if (owner.equals(player.getUniqueId())) {
            return; // cannot add owner to group lists
        }

        if (!groupMembers.containsKey(group)) {
            groupMembers.put(group, new Members());
        }

        for (Map.Entry<GroupType, Members> entry : groupMembers.entrySet()) {
            Members members = entry.getValue();
            if (entry.getKey().equals(group)) {
                members.add(player);
            } else {
                members.remove(player);
            }
            entry.setValue(members);
        }
    }

    public Boolean getFlag(GroupType group, GroupFlagType flag) {
        GroupFlags flags = groupFlags.get(group);
        return flags == null ? null : flags.getFlag(flag);
    }

    public void setFlag(GroupType group, GroupFlagType flag, Boolean value) {
        GroupFlags flags = groupFlags.get(group);
        if (flags == null) {
            flags = new GroupFlags();
        }
        flags.setFlag(flag, value);
        groupFlags.put(group, flags);
    }
}
