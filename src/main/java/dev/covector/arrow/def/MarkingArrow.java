package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MarkingArrow extends CustomArrow {
    private static Color color = Color.fromRGB(255, 226, 79);
    private static String name = "Marking Arrow";
    private int duration;

    public MarkingArrow(int duration) {
        this.duration = duration;
    }

    public void onHitGround(Player shooter, Arrow arrow, Location location) {
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) entity;
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));
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
}