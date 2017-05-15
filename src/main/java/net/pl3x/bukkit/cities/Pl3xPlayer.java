package net.pl3x.bukkit.cities;

import net.pl3x.bukkit.cities.claim.City;
import net.pl3x.bukkit.cities.claim.CityManager;
import net.pl3x.bukkit.cities.claim.Plot;
import net.pl3x.bukkit.cities.claim.region.flag.FlagType;
import net.pl3x.bukkit.cities.configuration.Config;
import net.pl3x.bukkit.cities.configuration.Lang;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Pl3xPlayer {
    private static final Map<Player, Pl3xPlayer> players = new HashMap<>();

    public static Pl3xPlayer getPlayer(Player player) {
        if (!players.containsKey(player)) {
            players.put(player, new Pl3xPlayer(player));
        }
        return players.get(player);
    }

    public static void remove(Player player) {
        Pl3xPlayer pl3xPlayer = players.get(player);
        if (pl3xPlayer != null) {
            pl3xPlayer.unload();
            players.remove(player);
        }
    }

    public static void unloadAll() {
        players.values().forEach(Pl3xPlayer::unload);
        players.clear();
    }

    private Player player;
    private City inCity;
    private Plot inPlot;

    private Pl3xPlayer(Player player) {
        this.player = player;
    }

    public void unload() {
        player = null;
        inCity = null;
        inPlot = null;
    }

    public Player getPlayer() {
        return player;
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
        if (plot == inPlot()) {
            return;
        }

        this.inPlot = plot;
    }

    public void updateLocation() {
        City inCity = CityManager.getInstance().getCity(player.getLocation());

        inCity(inCity);
        inPlot(inCity.getPlot(player.getLocation()));
    }
}
