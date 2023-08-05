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


public class DamageOverTimeArrow extends CustomArrow {
    private static Color color = Color.BLACK;
    private static String name = "Damage Over Time Arrow";
    private int duration;
    private int amplifier;

    public DamageOverTimeArrow(int duration, int amplifier) {
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public void onHitGround(Player shooter, Arrow arrow, Location location) {
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) entity;
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration * 20, amplifier));
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