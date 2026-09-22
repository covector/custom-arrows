package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import dev.covector.customarrows.arrow.CustomArrow;

public class TrueDamageArrow extends CustomArrow {
    private static Color color = Color.fromRGB(74, 10, 5);
    private String name = "True Damage Arrow";

    public void onHitGround(GroundHitEvent event) {
        event.arrow.remove();
    }

    public void onHitEntity(EntityHitEvent event) {
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(DamageEvent event) {
        LivingEntity entity = event.entity;
        double damage = event.damage;
        double scaledDamage = damage * 1.5D;
        if (entity instanceof Player) {
            return 0.1D;
        }
        if (entity.getHealth() - scaledDamage <= 0) {
            // entity.setHealth(0);
            return 999;
        }
        entity.setHealth(entity.getHealth() - scaledDamage);
        return 0.1D;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Deals (1.5*original damage) of true damage");
        return lore;
    }
}