package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public class HalfHealthArrow extends CustomArrow {
    private static Color color = Color.fromRGB(201, 119, 123);
    private String name = "Half Health Arrow";

    public void onHitGround(Player shooter, Arrow arrow, Location location) {
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(Player shooter, Arrow arrow, LivingEntity entity, double damage) {
        double maxhealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (entity.getHealth() > 0.5 * maxhealth) {
            return Math.min(entity.getHealth() - 0.5 * maxhealth, damage * 4);
        } else {
            entity.setHealth(Math.max(0.5 * maxhealth, entity.getHealth() - damage * 4));
            return 0;
        }
    }

    public String getName() {
        return name;
    }
}