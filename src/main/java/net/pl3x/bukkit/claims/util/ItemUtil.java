package net.pl3x.bukkit.claims.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
    public static ItemStack getItemInHand(Player player, EquipmentSlot hand) {
        return hand == EquipmentSlot.HAND ?
                player.getInventory().getItemInMainHand() :
                player.getInventory().getItemInOffHand();
    }

    public static boolean isMinecart(ItemStack item) {
        return item.getType() == Material.MINECART ||
                item.getType() == Material.POWERED_MINECART ||
                item.getType() == Material.STORAGE_MINECART ||
                item.getType() == Material.EXPLOSIVE_MINECART ||
                item.getType() == Material.HOPPER_MINECART;
    }

    public static boolean isBoat(ItemStack item) {
        return item.getType() == Material.BOAT ||
                item.getType() == Material.BOAT_ACACIA ||
                item.getType() == Material.BOAT_BIRCH ||
                item.getType() == Material.BOAT_DARK_OAK ||
                item.getType() == Material.BOAT_JUNGLE ||
                item.getType() == Material.BOAT_SPRUCE;
    }
}
