package net.pl3x.bukkit.claims.claim.group.trust;

import java.util.HashMap;
import java.util.Map;

public class Trusts {
    private final Map<TrustType, Boolean> trusts = new HashMap<>();

    public Boolean getTrust(TrustType trust) {
        return trusts.get(trust);
    }

    public void setTrust(TrustType trust, Boolean value) {
        if (value == null) {
            trusts.remove(trust);
            return;
        }
        trusts.put(trust, value);
    }
}
