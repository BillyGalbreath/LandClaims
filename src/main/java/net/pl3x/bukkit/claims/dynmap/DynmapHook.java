package net.pl3x.bukkit.claims.dynmap;

import net.pl3x.bukkit.claims.LandClaims;
import net.pl3x.bukkit.claims.claim.Claim;
import net.pl3x.bukkit.claims.claim.TrustType;
import net.pl3x.bukkit.claims.configuration.Config;
import net.pl3x.bukkit.claims.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import java.util.UUID;

public class DynmapHook {
    private final LandClaims plugin;

    private Map<String, AreaMarker> resAreas = new HashMap<>();
    private MarkerSet markerSetTop;
    private MarkerSet markerSetChild;
    private AreaStyle defStyle;
    private Map<String, AreaStyle> ownerStyle;

    boolean stop;

    public DynmapHook(LandClaims plugin) {
        this.plugin = plugin;
        MarkerAPI markerapi = ((DynmapAPI) plugin.getServer().getPluginManager().getPlugin("Dynmap")).getMarkerAPI();


        if (markerapi == null) {
            plugin.getLog().error("Error loading dynmap marker API!");
            return;
        }

        markerSetTop = markerapi.getMarkerSet("landclaims.markerset");
        if (markerSetTop == null) {
            markerSetTop = markerapi.createMarkerSet("landclaims.markerset", "Claims", null, false);
        } else {
            markerSetTop.setMarkerSetLabel("Claims");
        }
        if (markerSetTop == null) {
            plugin.getLog().error("Error creating top marker set");
            return;
        }

        markerSetChild = markerapi.getMarkerSet("landclaims.markersetchild");
        if (markerSetChild == null) {
            markerSetChild = markerapi.createMarkerSet("landclaims.markersetchild", "Child Claims", null, false);
        } else {
            markerSetChild.setMarkerSetLabel("Child Claims");
        }
        if (markerSetChild == null) {
            plugin.getLog().error("Error creating child marker set");
            return;
        }

        int minzoom = Config.DYNMAP_MIN_ZOOM;
        if (minzoom > 0) {
            markerSetTop.setMinZoom(minzoom);
            markerSetChild.setMinZoom(minzoom);
        }
        markerSetTop.setLayerPriority(Config.DYNMAP_LAYER_PRIORITY);
        markerSetTop.setHideByDefault(Config.DYNMAP_LAYER_HIDEBYDEFAULT);
        markerSetChild.setLayerPriority(Config.DYNMAP_LAYER_PRIORITY + 1);
        markerSetChild.setHideByDefault(Config.DYNMAP_LAYER_HIDEBYDEFAULT);

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
        new DynmapUpdate(this).runTaskTimerAsynchronously(plugin, 40, 1200);
    }

    private String getName(UUID uuid) {
        if (uuid.equals(Claim.PUBLIC_UUID)) {
            return Lang.TRUST_PUBLIC;
        } else {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
    }

    private String formatInfoWindow(Claim claim) {
        Collection<String> builders = new HashSet<>();
        Collection<String> containers = new HashSet<>();
        Collection<String> accessors = new HashSet<>();
        Collection<String> managers = new HashSet<>();

        claim.getTrusts().forEach((uuid, trustType) -> {
            if (trustType == TrustType.BUILDER) {
                builders.add(getName(uuid));
            } else if (trustType == TrustType.CONTAINER) {
                containers.add(getName(uuid));
            } else {
                accessors.add(getName(uuid));
            }
        });

        claim.getManagers().forEach(uuid -> {
            managers.add(getName(uuid));
        });

        return ("<div class=\"regioninfo\">" + (claim.isAdminClaim() ? Config.DYNMAP_ADMIN_WINDOW : Config.DYNMAP_INFO_WINDOW) + "</div>")
                .replace("%owner%", claim.getOwnerName())
                .replace("%dimensions%", claim.getCoordinates().getWidthX() + "x" + claim.getCoordinates().getWidthZ())
                .replace("%area%", Integer.toString(claim.getCoordinates().getArea()))
                .replace("%lastactive%", claim.getLastActive() + " days ago")
                .replace("%builders%", String.join(", ", builders))
                .replace("%containers%", String.join(", ", containers))
                .replace("%accessors%", String.join(", ", accessors))
                .replace("%managers%", String.join(", ", managers))
                .replace("%flags%", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', claim.getFlagsList()))
                        .replace("\n", "<br>"));
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
            return !(Config.DYNMAP_HIDDEN_REGIONS.contains(ownerName) ||
                    Config.DYNMAP_HIDDEN_REGIONS.contains("world:" + worldName) ||
                    Config.DYNMAP_HIDDEN_REGIONS.contains(worldName + "/" + ownerName));

        }
        return true;
    }

    private void addStyle(Claim claim, AreaMarker marker) {
        AreaStyle as = defStyle;

        // default claims are green
        int sc = 0x00FF00;
        int fc = 0x00FF00;

        // stale claims are red (older than 14 days inactive)
        if (claim.getLastActive() > 14) {
            sc = 0xFF0000;
            fc = 0xFF0000;
        }

        // admin claims are blue
        if (claim.isAdminClaim()) {
            sc = 0x0000FF;
            fc = 0x0000FF;
        }

        // child claims are white
        if (claim.getParent() != null) {
            sc = 0xFFFFFF;
            fc = 0xFFFFFF;
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
        String ownerName = claim.getOwnerName();

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
                if (claim.getParent() == null) {
                    marker = markerSetTop.createAreaMarker(markerid, ownerName, false, worldName, x, z, false);
                } else {
                    marker = markerSetChild.createAreaMarker(markerid, ownerName, false, worldName, x, z, false);
                }
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

            addStyle(claim, marker);
            marker.setDescription(formatInfoWindow(claim));
            newmap.put(markerid, marker);
        }
    }

    void updateClaims() {
        Map<String, AreaMarker> newMap = new HashMap<>();
        Collection<Claim> topLevelClaims = plugin.getClaimManager().getTopLevelClaims();
        if (topLevelClaims != null) {
            topLevelClaims.forEach(claim -> handleClaim(claim, newMap));
            topLevelClaims.stream()
                    .filter(claim -> !claim.getChildren().isEmpty())
                    .flatMap(claim -> claim.getChildren().stream())
                    .forEach(child -> handleClaim(child, newMap));
        }
        resAreas.values().forEach(GenericMarker::deleteMarker);
        resAreas = newMap;
    }

    public void disable() {
        stop = true;
        if (markerSetTop != null) {
            markerSetTop.deleteMarkerSet();
            markerSetTop = null;
        }
        if (markerSetChild != null) {
            markerSetChild.deleteMarkerSet();
            markerSetChild = null;
        }
        resAreas.clear();
    }
}
