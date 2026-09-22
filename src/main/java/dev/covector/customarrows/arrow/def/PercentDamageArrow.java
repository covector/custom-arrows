package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import dev.covector.customarrows.arrow.CustomArrow;

public class PercentDamageArrow extends CustomArrow {
    private static Color color = Color.fromRGB(74, 10, 5);
    private static String name = "Percent Damage Arrow";
    private double percent;

    public PercentDamageArrow(double percent) {
        this.percent = percent;
    }

    public void onHitGround(GroundHitEvent event) {
        event.arrow.remove();
    }

    public void onHitEntity(EntityHitEvent event) {
        // -1 health
        event.shooter.setHealth(event.shooter.getHealth() - 1);
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(DamageEvent event) {
        LivingEntity entity = event.entity;
        double damage = event.damage;
        return Math.min(percent * entity.getHealth(), damage * 2.5D);
    }
    
    public String getName() {
        return name;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Deals " + String.valueOf(percent * 100) + "% of target's health");
        lore.add(ChatColor.GRAY + "Damage capped at 2.5x arrow damage");
        return lore;
    }
}