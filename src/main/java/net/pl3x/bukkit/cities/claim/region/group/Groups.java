package net.pl3x.bukkit.cities.claim.region.group;

import net.pl3x.bukkit.cities.claim.region.group.trust.TrustType;
import net.pl3x.bukkit.cities.claim.region.group.trust.Trusts;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Groups {
    private final UUID owner;
    private final Map<GroupType, Members> groupMembers = new HashMap<>();
    private final Map<GroupType, Trusts> groupTrusts = new HashMap<>();

    public Groups(UUID owner) {
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

    public Boolean getTrust(GroupType group, TrustType trust) {
        Trusts trusts = groupTrusts.get(group);
        return trusts == null ? null : trusts.getTrust(trust);
    }

    public void setTrust(GroupType group, TrustType trust, Boolean value) {
        Trusts trusts = groupTrusts.get(group);
        if (trusts == null) {
            trusts = new Trusts();
        }
        trusts.setTrust(trust, value);
        groupTrusts.put(group, trusts);
    }
}
