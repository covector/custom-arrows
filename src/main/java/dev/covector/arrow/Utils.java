package dev.covector.customarrows.arrow;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static List<Entity> getNearbyChunkEntities(Location loc) {
        List<Entity> entities = new ArrayList<>();

        int cx = loc.getChunk().getX();
        int cz = loc.getChunk().getZ();

        for (int x = -1; x < 2; x++)
            for (int z = -1; z < 2; z++)
                entities.addAll(Arrays.asList(loc.getWorld().getChunkAt(cx + x, cz + z).getEntities()));

        return entities;
    }

    public static boolean validTarget(Entity entity) {
        return entity instanceof LivingEntity && !(entity instanceof ArmorStand) && !(entity instanceof Player);
    }

}