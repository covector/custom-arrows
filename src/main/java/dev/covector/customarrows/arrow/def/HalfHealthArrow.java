package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import dev.covector.customarrows.arrow.CustomArrow;

public class HalfHealthArrow extends CustomArrow {
    private static Color color = Color.fromRGB(74, 10, 5);
    private String name = "Half Health Arrow";

    public void onHitGround(LivingEntity shooter, Arrow arrow, Location location, BlockFace blockFace) {
        arrow.remove();
    }

    public void onHitEntity(LivingEntity shooter, Arrow arrow, Entity entity) {
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(LivingEntity shooter, Arrow arrow, LivingEntity entity, double damage) {
        double maxhealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (entity.getHealth() > 0.5 * maxhealth) {
            if (entity instanceof Player) {
                entity.setHealth(0.5 * maxhealth);
                return 0;
            }
            return Math.min(entity.getHealth() - 0.5 * maxhealth, damage * 4.2D);
        } else {
            entity.setHealth(0.5 * maxhealth);
            return 0;
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Cut hit entity's health in half");
        lore.add(ChatColor.GRAY + "Damage capped at 4.20x arrow damage");
        lore.add(ChatColor.GRAY + "Can affect players");
        return lore;
    }
}