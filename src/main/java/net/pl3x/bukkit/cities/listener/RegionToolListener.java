package net.pl3x.bukkit.cities.listener;

import net.pl3x.bukkit.cities.Pl3xCities;
import net.pl3x.bukkit.cities.configuration.Config;
import net.pl3x.bukkit.cities.player.Pl3xPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;

public class RegionToolListener implements Listener {
    private final Pl3xCities plugin;

    public RegionToolListener(Pl3xCities plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // only care about main hand packet
        }

        Player player = event.getPlayer();
        if (Config.isWorldDisabled(player.getWorld())) {
            return; // cities not enabled in this world
        }

        Block clickedBlock = null;
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            // get block looking at
            clickedBlock = player.getTargetBlock((HashSet<Material>) null, 100);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // get clicked block
            clickedBlock = event.getClickedBlock();
        }

        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            return; // no block clicked
        }

        if (!Config.isRegionWand(player.getInventory().getItemInMainHand())) {
            return; // not using region wand
        }

        Pl3xPlayer pl3xPlayer = Pl3xPlayer.getPlayer(player);
        if (pl3xPlayer.getSelection() == null) {
            // make a new selection
            pl3xPlayer.setSelection(clickedBlock.getLocation());
        } else {
            // finish the selection

        }
    }
}
