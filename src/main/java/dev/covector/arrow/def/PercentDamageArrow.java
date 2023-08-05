package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

public class PercentDamageArrow extends CustomArrow {
    private static Color color = Color.fromRGB(41, 7, 3);
    private static String name = "Percent Damage Arrow";
    private double percent;

    public PercentDamageArrow(double percent) {
        this.percent = percent;
    }

    public void onHitGround(Player shooter, Arrow arrow, Location location) {
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(Player shooter, Arrow arrow, LivingEntity entity, double damage) {
        return Math.min(percent * entity.getHealth(), damage * 4);
    }
    
    public String getName() {
        return name;
    }
}