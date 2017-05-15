package net.pl3x.bukkit.cities.player;

import net.pl3x.bukkit.cities.claim.City;
import net.pl3x.bukkit.cities.claim.CityManager;
import net.pl3x.bukkit.cities.claim.Plot;
import net.pl3x.bukkit.cities.claim.region.Region;
import net.pl3x.bukkit.cities.claim.region.flag.FlagType;
import net.pl3x.bukkit.cities.configuration.Config;
import net.pl3x.bukkit.cities.configuration.Lang;
import net.pl3x.bukkit.cities.configuration.PlayerConfig;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Pl3xPlayer extends PlayerConfig {
    private static final Map<Player, Pl3xPlayer> players = new HashMap<>();

    public static Pl3xPlayer getPlayer(Player player) {
        if (!players.containsKey(player)) {
            players.put(player, new Pl3xPlayer(player));
        }
        return players.get(player);
    }

    public static void unload(Player player) {
        if (players.containsKey(player)) {
            players.get(player).unload();
            players.remove(player);
        }
    }

    public static void unloadAll() {
        players.values().forEach(Pl3xPlayer::unload);
        players.clear();
    }

    public static void reloadAll() {
        players.values().forEach(Pl3xPlayer::reload);
    }

    private Player player;
    private Location lastLocation;
    private ToolMode toolMode = ToolMode.CITY;
    private Location selection;
    private Region resizingRegion;
    private City plottingCity;
    private City inCity;
    private Plot inPlot;

    private Pl3xPlayer(Player player) {
        super(player.getUniqueId());
        this.player = player;
    }

    public void unload() {
        player = null;
        lastLocation = null;
        toolMode = null;
        selection = null;
        resizingRegion = null;
        plottingCity = null;
        inCity = null;
        inPlot = null;
    }

    public Player getPlayer() {
        return player;
    }

    public int getClaimBlocks() {
        return getInt("claim-blocks", 0);
    }

    public void setClaimBlocks(int claimBlocks) {
        set("claim-blocks", claimBlocks);
        save();
    }

    public int getBonusBlocks() {
        return getInt("bonus-blocks", 0);
    }

    public void setBonusBlocks(int bonusBlocks) {
        set("bonus-blocks", bonusBlocks);
        save();
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void updateLocation() {
        this.lastLocation = player.getLocation();
        City inCity = CityManager.getInstance().getCity(lastLocation);
        inCity(inCity);
        inPlot(inCity.getPlot(lastLocation));
    }

    public ToolMode getToolMode() {
        return toolMode;
    }

    public void setToolMode(ToolMode toolMode) {
        this.toolMode = toolMode;
    }

    public Location getSelection() {
        return selection;
    }

    public void setSelection(Location location) {
        this.selection = location;
    }

    public Region getResizingRegion() {
        return resizingRegion;
    }

    public void setResizingRegion(Region resizingRegion) {
        this.resizingRegion = resizingRegion;
    }

    public City getPlottingCity() {
        return plottingCity;
    }

    public void setPlottingCity(City plottingCity) {
        this.plottingCity = plottingCity;
    }

    public City inCity() {
        return inCity;
    }

    public void inCity(City city) {
        if (city == inCity()) {
            return; // same city
        }

        // set new city (or null)
        this.inCity = city;

        // don't display a title/actionbar for a disabled world
        if (Config.isWorldDisabled(player.getLocation().getWorld())) {
            return; // cities not enabled in this world
        }

        String cityName = city == null ? Config.WILD_NAME : city.getName();
        Boolean pvpFlag = city == null ? Config.WILD_PVP : city.getFlags().getFlag(FlagType.PVP);
        String pvp = BooleanUtils.toStringOnOff(pvpFlag == null ? FlagType.PVP.getDefault() : pvpFlag);
        player.sendTitle(
                Lang.CITY_CHANGE_TITLE
                        .replace("{city-name}", cityName)
                        .replace("{pvp}", pvp),
                Lang.CITY_CHANGE_SUBTITLE
                        .replace("{city-name}", cityName)
                        .replace("{pvp}", pvp),
                Config.TITLE_TIME_FADE_IN, Config.TITLE_TIME_STAY, Config.TITLE_TIME_FADE_OUT);
        player.sendActionBar('&', Lang.CITY_CHANGE_ACTIONBAR
                .replace("{city-name}", cityName)
                .replace("{pvp}", pvp));

    }

    public Plot inPlot() {
        return inPlot;
    }

    public void inPlot(Plot plot) {
        if (plot == inPlot) {
            return;
        }

        this.inPlot = plot;
    }
}
