package net.pl3x.bukkit.cities.claim;

import net.pl3x.bukkit.cities.claim.region.Coordinates;
import net.pl3x.bukkit.cities.claim.region.Region;

import java.util.UUID;

public class Plot extends Region {
    private final long cityId;

    public Plot(long id, long cityId, UUID owner, Coordinates coordinates) {
        super(id, owner, coordinates);

        this.cityId = cityId;
    }

    public Plot(long parent, UUID owner, Coordinates coordinates) {
        this(getNextId(), parent, owner, coordinates);
    }

    public long getCityId() {
        return cityId;
    }

    public City getCity() {
        return CityManager.getInstance().getCity(getCityId());
    }
}
