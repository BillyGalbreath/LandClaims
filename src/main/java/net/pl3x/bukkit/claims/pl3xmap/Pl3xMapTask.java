package net.pl3x.bukkit.claims.pl3xmap;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.MapWorld;
import net.pl3x.map.api.Point;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import net.pl3x.map.api.marker.Rectangle;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.util.Collection;

public class Pl3xMapTask extends BukkitRunnable {
    private final LandClaims plugin;
    private final MapWorld world;
    private final SimpleLayerProvider provider;

    private boolean stop;

    public Pl3xMapTask(LandClaims plugin, MapWorld world, SimpleLayerProvider provider) {
        this.plugin = plugin;
        this.world = world;
        this.provider = provider;
    }

    @Override
    public void run() {
        if (stop) {
            cancel();
        }
        updateClaims();
    }

    void updateClaims() {
        provider.clearMarkers(); // TODO track markers instead of clearing them
        Collection<Claim> topLevelClaims = this.plugin.getClaimManager().getTopLevelClaims();
        if (topLevelClaims != null) {
            topLevelClaims.stream()
                    .filter(claim -> claim.getWorld().getUID().equals(this.world.uuid()))
                    .forEach(this::handleClaim);
        }
    }

    private void handleClaim(Claim claim) {
        Location min = claim.getCoordinates().getMinLocation();
        Location max = claim.getCoordinates().getMaxLocation();
        if (min == null) {
            return;
        }

        String worldName = min.getWorld().getName();
        String ownerName = claim.getOwnerName();

        Rectangle rect = Marker.rectangle(Point.of(min.getBlockX(), min.getBlockZ()), Point.of(max.getBlockX() + 1, max.getBlockZ() + 1));

        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Color.GREEN)
                .fillColor(Color.GREEN)
                .fillOpacity(0.2)
                .clickTooltip("Region owned by<br/>" + ownerName);

        if (claim.isAdminClaim()) {
            options.strokeColor(Color.BLUE).fillColor(Color.BLUE);
        } else if (claim.getLastActive() > 14) {
            options.strokeColor(Color.RED).fillColor(Color.RED);
        }

        rect.markerOptions(options);

        String markerid = "landclaims_" + worldName + "region_" + Long.toHexString(claim.getId());
        this.provider.addMarker(Key.of(markerid), rect);
    }

    public void disable() {
        cancel();
        this.stop = true;
        this.provider.clearMarkers();
    }
}
