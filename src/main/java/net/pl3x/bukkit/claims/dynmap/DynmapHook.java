package net.pl3x.bukkit.claims.dynmap;

import net.pl3x.bukkit.claims.Pl3xClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.TrustType;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.GenericMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DynmapHook {
    private final Pl3xClaims plugin;

    private static final String ADMIN_ID = "administrator";

    private Map<String, AreaMarker> resAreas = new HashMap<>();
    private MarkerSet markerSet;
    private AreaStyle defStyle;
    private Map<String, AreaStyle> ownerStyle;

    boolean stop;

    public DynmapHook(Pl3xClaims plugin) {
        this.plugin = plugin;
        DynmapAPI api = (DynmapAPI) plugin.getServer().getPluginManager().getPlugin("Dynmap");
        MarkerAPI markerapi = api.getMarkerAPI();

        if (markerapi == null) {
            plugin.getLog().error("Error loading dynmap marker API!");
            return;
        }

        markerSet = markerapi.getMarkerSet("pl3xclaims.markerset");
        if (markerSet == null) {
            markerSet = markerapi.createMarkerSet("pl3xclaims.markerset", Config.DYNMAP_LAYER_NAME, null, false);
        } else {
            markerSet.setMarkerSetLabel(Config.DYNMAP_LAYER_NAME);
        }
        if (markerSet == null) {
            plugin.getLog().error("Error creating marker set");
            return;
        }

        int minzoom = Config.DYNMAP_MIN_ZOOM;
        if (minzoom > 0) {
            markerSet.setMinZoom(minzoom);
        }
        markerSet.setLayerPriority(Config.DYNMAP_LAYER_PRIORITY);
        markerSet.setHideByDefault(Config.DYNMAP_LAYER_HIDEBYDEFAULT);

        /* Get style information */
        defStyle = new AreaStyle(plugin.getConfig(), "regionstyle");
        ownerStyle = new HashMap<>();
        ConfigurationSection sect = plugin.getConfig().getConfigurationSection("ownerstyle");
        if (sect != null) {
            sect.getKeys(false).forEach(id ->
                    ownerStyle.put(id.toLowerCase(),
                            new AreaStyle(plugin.getConfig(),
                                    "ownerstyle." + id, defStyle)));
        }

        stop = false;
        new DynmapUpdate(this).runTaskTimer(plugin, 40, 1200);
    }

    private String formatInfoWindow(Claim claim) {
        Collection<String> builders = new HashSet<>();
        Collection<String> containers = new HashSet<>();
        Collection<String> accessors = new HashSet<>();
        Collection<String> managers = new HashSet<>();

        claim.getTrusts().forEach((uuid, trustType) -> {
            String targetName = null;
            if (uuid == null) {
                targetName = Lang.TRUST_PUBLIC;
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                if (target != null) {
                    targetName = target.getName();
                }
            }
            if (targetName != null) {
                if (trustType == TrustType.BUILDER) {
                    builders.add(targetName);
                } else if (trustType == TrustType.CONTAINER) {
                    containers.add(targetName);
                } else {
                    accessors.add(targetName);
                }
            }
        });

        claim.getManagers().forEach(uuid -> {
            String targetName = null;
            if (uuid == null) {
                targetName = Lang.TRUST_PUBLIC;
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                if (target != null) {
                    targetName = target.getName();
                }
            }
            managers.add(targetName);
        });

        return ("<div class=\"regioninfo\">" + (claim.isAdminClaim() ? Config.DYNMAP_ADMIN_WINDOW : Config.DYNMAP_INFO_WINDOW) + "</div>")
                .replace("%owner%", claim.isAdminClaim() ? ADMIN_ID : Bukkit.getOfflinePlayer(claim.getOwner()).getName())
                .replace("%area%", Integer.toString(claim.getCoordinates().getArea()))
                .replace("%builders%", String.join(", ", builders))
                .replace("%containers%", String.join(", ", containers))
                .replace("%accessors%", String.join(", ", accessors))
                .replace("%managers%", String.join(", ", managers));
    }

    private boolean isVisible(String ownerName, String worldName) {
        if (Config.DYNMAP_VISIBLE_REGIONS != null && !Config.DYNMAP_VISIBLE_REGIONS.isEmpty()) {
            if (Config.DYNMAP_VISIBLE_REGIONS.contains(ownerName) ||
                    Config.DYNMAP_VISIBLE_REGIONS.contains("world:" + worldName) ||
                    Config.DYNMAP_VISIBLE_REGIONS.contains(worldName + "/" + ownerName)) {
                return false;
            }
        }
        if (Config.DYNMAP_HIDDEN_REGIONS != null && !Config.DYNMAP_HIDDEN_REGIONS.isEmpty()) {
            if (Config.DYNMAP_HIDDEN_REGIONS.contains(ownerName) ||
                    Config.DYNMAP_HIDDEN_REGIONS.contains("world:" + worldName) ||
                    Config.DYNMAP_HIDDEN_REGIONS.contains(worldName + "/" + ownerName))
                return false;
        }
        return true;
    }

    private void addStyle(String owner, AreaMarker marker) {
        AreaStyle as = null;

        if (!ownerStyle.isEmpty()) {
            as = ownerStyle.get(owner.toLowerCase());
        }
        if (as == null) {
            as = defStyle;
        }

        int sc = 0xFF0000;
        int fc = 0xFF0000;
        try {
            sc = Integer.parseInt(as.strokeColor.substring(1), 16);
            fc = Integer.parseInt(as.fillColor.substring(1), 16);
        } catch (NumberFormatException ignore) {
        }
        marker.setLineStyle(as.strokeWeight, as.strokeOpacity, sc);
        marker.setFillStyle(as.fillOpacity, fc);
        if (as.label != null) {
            marker.setLabel(as.label);
        }
    }

    private void handleClaim(Claim claim, Map<String, AreaMarker> newmap) {
        Location min = claim.getCoordinates().getMinLocation();
        Location max = claim.getCoordinates().getMaxLocation();
        if (min == null) {
            return;
        }
        String worldName = min.getWorld().getName();
        String ownerName = claim.isAdminClaim() ? ADMIN_ID : Bukkit.getOfflinePlayer(claim.getOwner()).getName();

        double[] x;
        double[] z;
        if (isVisible(ownerName, worldName)) {
            x = new double[4];
            z = new double[4];
            x[0] = min.getX();
            z[0] = min.getZ();
            x[1] = min.getX();
            z[1] = max.getZ() + 1.0;
            x[2] = max.getX() + 1.0;
            z[2] = max.getZ() + 1.0;
            x[3] = max.getX() + 1.0;
            z[3] = min.getZ();

            String markerid = "PC_" + Long.toHexString(claim.getId());
            AreaMarker marker = resAreas.remove(markerid);
            if (marker == null) {
                marker = markerSet.createAreaMarker(markerid, ownerName, false, worldName, x, z, false);
                if (marker == null) {
                    return;
                }
            } else {
                marker.setCornerLocations(x, z);
                marker.setLabel(ownerName);
            }
            if (Config.DYNMAP_3D_REGIONS) {
                marker.setRangeY(max.getY() + 1.0, min.getY());
            }

            addStyle(ownerName, marker);
            marker.setDescription(formatInfoWindow(claim));
            newmap.put(markerid, marker);
        }
    }

    void updateClaims() {
        Map<String, AreaMarker> newMap = new HashMap<>();
        Collection<Claim> claims = plugin.getClaimManager().getTopLevelClaims();
        if (claims != null) {
            claims.forEach(claim -> handleClaim(claim, newMap));
            claims.stream()
                    .filter(claim -> !claim.getChildren().isEmpty())
                    .flatMap(claim -> claim.getChildren().stream())
                    .forEach(child -> handleClaim(child, newMap));
        }
        resAreas.values().forEach(GenericMarker::deleteMarker);
        resAreas = newMap;
    }

    public void disable() {
        stop = true;
        if (markerSet != null) {
            markerSet.deleteMarkerSet();
            markerSet = null;
        }
        resAreas.clear();
    }
}
