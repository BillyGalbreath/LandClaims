package net.pl3x.bukkit.cities.claim;

import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CityManager {
    private static final CityManager instance = new CityManager();

    public static CityManager getInstance() {
        return instance;
    }

    private CityManager() {
    }

    private final Map<Long, City> citiesById = new HashMap<>();
    private final Map<String, City> citiesByName = new HashMap<>();

    public Collection<City> getCities() {
        return citiesById.values();
    }

    public City getCity(long id) {
        return citiesById.get(id);
    }

    public City getCity(String name) {
        return citiesByName.get(name);
    }

    public City getCity(Location location) {
        for (City city : getCities()) {
            if (city.contains(location)) {
                return city;
            }
        }
        return null;
    }

    public void addCity(City city) {
        citiesById.put(city.getId(), city);
        citiesByName.put(city.getName(), city);
    }

    public void loadCities() {
        //
    }

    public void unloadCities() {
        citiesByName.values().forEach(City::unload);
        citiesByName.clear();
        citiesById.clear();
    }
}
