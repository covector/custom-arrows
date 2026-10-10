package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.CustomArrow;

public class PercentDamageArrow extends CustomArrow {
    private static Color color = Color.fromRGB(74, 10, 5);
    private static String name = "Percent Damage Arrow";
    private double percent;
    private double damageCap = 0.1D;
    private double selfDamage = 1D;
    public NamespacedKey originalDamageKey = new NamespacedKey(CustomArrowsPlugin.plugin, "original-damage");

    public PercentDamageArrow(double percent) {
        this.percent = percent;
    }

    public void onHitGround(GroundHitEvent event) {
    }

    public void onHitEntity(EntityHitEvent event) {
        // -1 health
        event.shooter.setHealth(Math.max(0, event.shooter.getHealth() - selfDamage));
        event.shooter.damage(0.1);
    }

    public Color getColor() {
        return color;
    }

    protected void storeOriginalDamage(DamageEvent event) {
        Arrow arrow = event.arrow;
        if (!arrow.getPersistentDataContainer().has(originalDamageKey, PersistentDataType.DOUBLE)) {
            arrow.getPersistentDataContainer().set(originalDamageKey, PersistentDataType.DOUBLE, event.damage);
        }
    }

    protected double getOriginalDamage(DamageEvent event) {
        Arrow arrow = event.arrow;
        if (!arrow.getPersistentDataContainer().has(originalDamageKey, PersistentDataType.DOUBLE)) {
            return event.damage;
        }
        return arrow.getPersistentDataContainer().get(originalDamageKey, PersistentDataType.DOUBLE);
    }

    public double ModifyDamage(DamageEvent event) {
        LivingEntity entity = event.entity;
        double shooterHealth = event.shooter.getHealth();
        storeOriginalDamage(event);
        double damage = getOriginalDamage(event);
        return Math.min(percent * entity.getHealth(), shooterHealth * damage * damageCap);
    }

    public boolean removeOnHitGround() {
        return true;
    }
    
    public String getName() {
        return name;
    }

    public boolean allowTrigger() {
        return true;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Deals " + String.valueOf(percent * 100) + "% of target's health");
        lore.add(ChatColor.GRAY + "Damage capped at (" + String.valueOf(damageCap) + " * shooter's health * bow damage)");
        lore.add(ChatColor.GRAY + "Won't affect players");
        lore.add(ChatColor.GRAY + "Will damage shooter by " + String.valueOf(selfDamage) + " health");
        return lore;
    }
}