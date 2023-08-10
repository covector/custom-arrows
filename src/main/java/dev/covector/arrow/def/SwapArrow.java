package dev.covector.customarrows.arrow;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class SwapArrow extends PierceAwareArrow {
    private static Color color = Color.PURPLE;
    private static String name = "Swap Arrow";

    public void onAfterHitAll(Player shooter, Arrow arrow, Entity[] entities) {
        if (entities.length == 0) return;
        int firstLivingEntityIndex = -1;
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] instanceof LivingEntity && !(entities[i].getUniqueId().toString().equals(shooter.getUniqueId().toString()))) {
                firstLivingEntityIndex = i;
                break;
            }
        }
        if (firstLivingEntityIndex == -1) return;
        Location loc = entities[firstLivingEntityIndex].getLocation().clone();
        for (Entity e : entities) {
            if (e instanceof LivingEntity && !(e.getUniqueId().toString().equals(shooter.getUniqueId().toString()))) {
                e.teleport(shooter.getLocation().clone());
            }
        }
        shooter.getWorld().playSound(shooter.getLocation().clone(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        shooter.teleport(loc);
        shooter.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(Player shooter, Arrow arrow, LivingEntity entity, double damage) {
        return -1;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Swap positions with hit entity");
        return lore;
    }
}