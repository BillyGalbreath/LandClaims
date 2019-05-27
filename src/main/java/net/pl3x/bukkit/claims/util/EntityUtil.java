package net.pl3x.bukkit.claims.util;

import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.WaterMob;

public class EntityUtil {
    public static boolean isAnimal(Entity entity) {
        return entity instanceof Animals ||
                entity instanceof Ambient || // bat
                entity instanceof WaterMob || // squid
                entity instanceof IronGolem ||
                entity instanceof Snowman;
    }

    public static boolean isMob(Entity entity) {
        return entity instanceof Monster ||
                entity instanceof Slime || // slime / magmacube
                entity instanceof Ghast ||
                entity instanceof Shulker ||
                entity instanceof EnderDragon ||
                entity instanceof Phantom;
    }
}
