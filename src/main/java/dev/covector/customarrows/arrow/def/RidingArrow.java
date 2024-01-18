package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RidingArrow extends PierceAwareArrow {
    private static Color color = Color.fromRGB(16, 16, 156);
    private static String name = "Riding Arrow";
    private HashMap<String, LivingEntity> riders = new HashMap<String, LivingEntity>();

    public void onAfterHitAll(Player shooter, Arrow arrow, Entity[] entities) {
        if (entities.length == 0) return;
        int livingEntityCount = 0;
        int firstLivingEntityIndex = -1;
        for (int i = 0; i < entities.length; i++) {
            // if (entities[i] instanceof LivingEntity && !(entities[i] instanceof Player)) {
            if (entities[i] instanceof LivingEntity) {
                livingEntityCount++;
                if (firstLivingEntityIndex == -1) firstLivingEntityIndex = i;
            }
        }
        if (livingEntityCount == 0) return;
        String shooterUUID = shooter.getUniqueId().toString();
        Entity currentEntity = entities[firstLivingEntityIndex];
        if (livingEntityCount != 1) {
            for (int i = firstLivingEntityIndex + 1; i < entities.length; i++) {
                // if (entities[i] instanceof LivingEntity && !(entities[i] instanceof Player)) {
                if (entities[i] instanceof LivingEntity) {
                    currentEntity.addPassenger(entities[i]);
                    currentEntity = entities[i];
                }
            }
        }
        if (riders.containsKey(shooterUUID)) {
            if (!(riders.get(shooterUUID).isDead())) {
                currentEntity.addPassenger(riders.get(shooterUUID));
                riders.remove(shooterUUID);
            } else {
                riders.put(shooterUUID, (LivingEntity) currentEntity);
            }
        } else if (livingEntityCount == 1) {
            riders.put(shooterUUID, (LivingEntity) currentEntity);
        }
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
        lore.add(ChatColor.WHITE + "Make hit entities ride each other");
        lore.add(ChatColor.GRAY + "If only one entity hit");
        lore.add(ChatColor.GRAY + "It will ride the next entity hit");
        return lore;
    }
}