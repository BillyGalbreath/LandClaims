package net.pl3x.bukkit.claims.pl3xmap;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.util.Permission;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.MapWorld;
import net.pl3x.map.api.Point;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import net.pl3x.map.api.marker.Rectangle;
import org.bukkit.ChatColor;
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

        Rectangle rect = Marker.rectangle(Point.of(min.getBlockX(), min.getBlockZ()), Point.of(max.getBlockX() + 1, max.getBlockZ() + 1));
        Color color = color(claim);

        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(color)
                .fillColor(color)
                .fillOpacity(0.2)
                .clickTooltip(tooltip(claim));

        rect.markerOptions(options);

        String markerid = "landclaims_" + world.name() + "region_" + Long.toHexString(claim.getId());
        this.provider.addMarker(Key.of(markerid), rect);
    }

    private Color color(Claim claim) {
        if (claim.isAdminClaim()) {
            return Color.BLUE;
        } else if (claim.getLastActive() > 14) {
            return Color.RED;
        } else {
            return Color.GREEN;
        }
    }

    public static String tooltip(Claim claim) {
        Permission perm = new Permission(claim);

        return ("<div class=\"regioninfo\">" + (claim.isAdminClaim() ? Config.MAP_ADMIN_TOOLTIP : Config.MAP_TOOLIP) + "</div>")
                .replace("%owner%", claim.getOwnerName())
                .replace("%dimensions%", claim.getCoordinates().getWidthX() + "x" + claim.getCoordinates().getWidthZ())
                .replace("%area%", Integer.toString(claim.getCoordinates().getArea()))
                .replace("%lastactive%", claim.getLastActive() + " days ago")
                .replace("%builders%", perm.builders().isEmpty() ? "<em>n/a</em>" : String.join(", ", perm.builders()))
                .replace("%containers%", perm.containers().isEmpty() ? "<em>n/a</em>" : String.join(", ", perm.containers()))
                .replace("%accessors%", perm.accessors().isEmpty() ? "<em>n/a</em>" : String.join(", ", perm.accessors()))
                .replace("%managers%", perm.managers().isEmpty() ? "<em>n/a</em>" : String.join(", ", perm.managers()))
                .replace("%flags%", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', claim.getFlagsList()))
                        .replace("\n", "<br>"));
    }

    public void disable() {
        cancel();
        this.stop = true;
        this.provider.clearMarkers();
    }
}
