package net.pl3x.bukkit.cities.listener;

import net.pl3x.bukkit.cities.Pl3xCities;
import net.pl3x.bukkit.cities.claim.City;
import net.pl3x.bukkit.cities.claim.CityManager;
import net.pl3x.bukkit.cities.claim.region.flag.FlagType;
import net.pl3x.bukkit.cities.configuration.Config;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class ProtectionListener implements Listener {
    private final Pl3xCities plugin;

    public ProtectionListener(Pl3xCities plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (Config.isWorldDisabled(event.getBlock().getWorld())) {
            return; // cities not enabled in this world
        }

        if (!event.getSource().getType().equals(Material.FIRE)) {
            return; // not fire that is spreading
        }

        City fromCity = CityManager.getInstance().getCity(event.getSource().getLocation());
        City toCity = CityManager.getInstance().getCity(event.getBlock().getLocation());

        // allow fire spread if not crossing city border and city has firespread flag enabled
        if (fromCity == toCity && toCity.getFlags().getFlag(FlagType.FIRESPREAD)) {
            return;
        }

        // stop the fire from spreading
        event.setCancelled(true);
    }
}
